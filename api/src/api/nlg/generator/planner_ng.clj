(ns api.nlg.generator.planner-ng
  (:require [api.nlg.generator.ops :as ops]
            [api.nlg.generator.parser-ng :as parser]
            [api.nlg.generator.realizer :as realizer]
            [acc-text.nlg.dsl.core :as dsl]
            [acc-text.nlg.grammar :as ccg]
            [acc-text.nlg.grammar-generation.translate :as translate]
            [clojure.tools.logging :as log]))

(defn build-dp-instance
  "dp - a hashmap compiled with `compile-dp`
   data - a flat hashmap (represents CSV)
   returns: hashmap (context) which will be used to generate text"
  [dp]
  (loop [context {:dynamic        []
                  :static         []
                  :reader-profile :default}
         fs dp]
    (if (empty? fs)
      context
      (let [[head & tail] fs]
        (log/tracef "Head: %s" head)
        (recur (ops/merge-context context (into {} head)) tail)))))

(defn resolve-item-context
  [item]
  (let [type-name (get-in item [:attrs :type])
        default {:pos :NP}]
    (case type-name
      :product {:class "product"
                :pos   :NNP}
      :benefit {:class "benefit"
                :pos   :NP}
      :component {:class "component"
                  :pos   :NNP}
      :amr {:class "amr"
            :pos   :S}
      default)))

(defn resolve-morph-context
  [group]
  (map (fn
         [item]
         (let [name (case (string? (item :name))
                      true (item :name)
                      false ((item :name) :dyn-name))
               context (resolve-item-context item)]
           (dsl/morph-entry
             name
             (:pos context)
             {:class (:class context)})))
       group))

(defn resolve-lex-context
  [idx [k members]]
  (let [{:keys [pos _]} (resolve-item-context (first members))
        family-part (partial
                      dsl/family
                      (name k) pos true
                      (dsl/entry "Primary"
                                 (dsl/lf "X")
                                 (dsl/atomcat pos {:index (+ idx 10)}
                                              (dsl/fs-nomvar "index" "X"))))]
    (apply family-part
           (map (fn [m]
                  (let [name (case (string? (m :name))
                               true (m :name)
                               false ((m :name) :dyn-name))]
                    (dsl/member name)))
                members))))

(defn compile-custom-grammar
  [values]
  (log/debug "\n--------------------\nCompiling Grammar\n--------------------")
  (log/debugf "Dynamic values: %s" (pr-str values))
  (let [grammar-builder (ccg/build-grammar
                          {:types (ccg/build-types (list
                                                     {:name "sem-obj"}
                                                     {:name "phys-obj" :parents "sem-obj"}))
                           :rules (ccg/build-default-rules)})
        initial-families (list
                           ;; AND rule. Example: <word1> and <word2>
                           (dsl/family "coord.objects" :Conj true
                                       (dsl/entry
                                         "NP-Collective"
                                         "and"
                                         (dsl/lf "X0" "and"
                                                 (dsl/diamond "First"
                                                              {:nomvar   "L1"
                                                               :prop     "elem"
                                                               :diamonds (list
                                                                           (dsl/diamond "Item" {:nomvar "X1"})
                                                                           (dsl/diamond "Next" {:nomvar  "L2"
                                                                                                :prop    "elem"
                                                                                                :diamond (dsl/diamond "Item" {:nomvar "X2"})}))}))
                                         (dsl/>F
                                           \.
                                           (dsl/<B
                                             (dsl/atomcat :NP {}
                                                          (dsl/fs-feat "num" "pl")
                                                          (dsl/fs-nomvar "index" "X0"))
                                             (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X1")))
                                           (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X2")))))
                           ;; PROVIDES rule. Example: <word1> provides <word2>
                           (dsl/family "v.provide" :V false
                                       (dsl/entry
                                         "Primary"
                                         (dsl/lf "E" "[*DEFAULT*]"
                                                 (dsl/diamond "Thing" {:nomvar "X"})
                                                 (dsl/diamond "Benefit" {:nomvar "Y"}))
                                         (dsl/>F
                                           \>
                                           (dsl/<B
                                             (dsl/atomcat :S {} (dsl/fs-nomvar "index" "E"))
                                             (dsl/atomcat :NNP {} (dsl/fs-nomvar "index" "X")))
                                           (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "Y")))))
                           ;; Full consequence rule. <word1> results in <word2>
                           (dsl/family "cons2full" :Vp false
                                       (dsl/entry
                                         "Primary"
                                         (dsl/lf "H")
                                         (dsl/>F
                                           (dsl/<B
                                             (dsl/atomcat :S {:index "12"} (dsl/fs-nomvar "index" "X0"))
                                             (dsl/>F
                                               (dsl/atomcat :NNP {} (dsl/fs-nomvar "index" "X1"))
                                               (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X3"))))
                                           (dsl/atomcat :IN {} (dsl/fs-nomvar "index" "X2")))))
                           (dsl/family "IN" :IN true
                                       (dsl/entry
                                         "IN"
                                         "in"
                                         (dsl/lf "I")
                                         (dsl/atomcat :IN {} (dsl/fs-nomvar "index" "I")))))

        grouped (group-by (fn [item] (get-in item [:attrs :type])) values)
        initial-morph (list
                        (dsl/morph-entry "provides" :V {:stem "benefit" :class "purpose"})
                        (dsl/morph-entry "offers" :V {:stem "benefit" :class "purpose"})
                        (dsl/morph-entry "gives" :V {:stem "benefit" :class "purpose"})
                        (dsl/morph-entry "results" :Vp {:stem "consequence" :class "consequence"})
                        (dsl/morph-entry "in" :IN {:stem "in"}))
        morphology-context (map resolve-morph-context (vals grouped))
        generated-families (map-indexed resolve-lex-context grouped)
        lexicon (ccg/build-lexicon
                  {:families (map translate/family->entry (concat initial-families generated-families))
                   :morph    (map translate/morph->entry (concat initial-morph (flatten morphology-context)))
                   :macros   (list)})]
    (grammar-builder lexicon)))

(defn get-placeholder [item]
  (if (realizer/placeholder? item)
    (get-in item [:name :dyn-name])
    (item :name)))

(defn amr? [item] (get (:attrs item) :amr false))

(defn generate-templates
  "Takes context and generates numerous sentences. Picks random one"
  [grammar context]
  (let [dyn-values (map get-placeholder (remove amr? (:dynamic context)))
        values (concat (distinct (:static context)) dyn-values)
        _ (log/debugf "Context: %s" context)
        generated (apply (partial ccg/generate grammar) (ops/distinct-wordlist values))]
    {:context   context
     :templates generated}))

(defn build-segment
  [grammar segment]
  (map (partial generate-templates grammar)
       (map build-dp-instance segment)))

(defn render-segment
  [templates data]
  (let [realized (map (partial realizer/realize data) templates)
        _ (log/debugf "Realized: %s" (pr-str realized))
        sentences (map #(if (empty? %) "" (rand-nth %)) realized)]
    (realizer/join-sentences sentences)))

(defn render-dp
  "document-plan - a hash map with document plan
   data - list of rows of hashmap (represents CSV)
   reader-profile - key defining reader (user)
   returns: generated text"
  [document-plan data reader-profile]
  (log/debugf "Given document plan: %s" document-plan)
  (let [dp (parser/parse-document-plan document-plan {} {:reader-profile reader-profile})
        instances (map #(map build-dp-instance %) dp)
        context (ops/merge-contexts {:static [] :dynamic []} instances)
        g (compile-custom-grammar
            (remove
              (fn [item] (get-in item [:attrs :amr]))
              (:dynamic context)))
        templates (map (partial build-segment g) dp)]
    (log/debugf "Templates: %s" (pr-str templates))
    (map (fn [row] (->> templates
                        (map #(render-segment % row))
                        (realizer/join-segments)))
         data)))
