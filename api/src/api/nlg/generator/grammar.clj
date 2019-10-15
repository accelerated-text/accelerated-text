(ns api.nlg.generator.grammar
  (:require [acc-text.nlg.dsl.core :as dsl]
            [acc-text.nlg.grammar :as ccg]
            [acc-text.nlg.grammar-generation.translate :as translate]
            [clojure.tools.logging :as log]))

(defn resolve-item-context [item]
  (case (get-in item [:attrs :type])
    :product   {:class "product" :pos :NNP}
    :benefit   {:class "benefit" :pos :NP}
    :component {:class "component" :pos :NNP}
    :modifier  {:class "modifier" :pos :ADJ}
    :amr       {:class "amr" :pos :S}
    {:pos :NP}))

(defn resolve-morph-context [group]
  (map (fn [{:keys [name] :as item}]
         (let [context (resolve-item-context item)]
           (dsl/morph-entry
             (if (string? name) name (:dyn-name name))
             (:pos context)
             {:class (:class context)})))
       group))

(defn resolve-lex-context
  [idx [k members]]
  (let [{:keys [pos]} (resolve-item-context (first members))
        family-part (partial
                      dsl/family
                      (name (or k "Cell")) pos true
                      (dsl/entry "Primary"
                                 (dsl/lf "X")
                                 (dsl/atomcat pos {:index (+ idx 10)}
                                              (dsl/fs-nomvar "index" "X"))))]
    (apply family-part
           (map (fn [{:keys [name]}]
                  (dsl/member (if (string? name) name (:dyn-name name))))
                members))))

(def grammar-builder (ccg/build-grammar
                      {:types (ccg/build-types (list
                                                {:name "sem-obj"}
                                                {:name "phys-obj" :parents "sem-obj"}))
                       :rules (ccg/build-default-rules)}))

(def initial-families (list
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
                                    (dsl/atomcat :IN {} (dsl/fs-nomvar "index" "I"))))

                       (dsl/family "Modifier" :ADJ false
                                   (dsl/entry
                                    "Primary"
                                    (dsl/lf "E" "[*DEFAULT*]"
                                            (dsl/diamond "Modifier" {:nomvar "X"})
                                            (dsl/diamond "Thing" {:nomvar "Y"}))
                                    (dsl/>F
                                               (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X"))
                                               (dsl/atomcat :xNP {} (dsl/fs-nomvar "index" "Y")))))))

(def initial-morph (list
                    (dsl/morph-entry "provides" :V {:stem "benefit" :class "purpose"})
                    (dsl/morph-entry "offers" :V {:stem "benefit" :class "purpose"})
                    (dsl/morph-entry "gives" :V {:stem "benefit" :class "purpose"})
                    (dsl/morph-entry "results" :Vp {:stem "consequence" :class "consequence"})
                    (dsl/morph-entry "in" :IN {:stem "in"})))


(defn compile-custom-grammar
  [values]
  (log/debugf "Dynamic values: %s" (pr-str values))
  (let [grouped (group-by (fn [item] (get-in item [:attrs :type])) values)
        morphology-context (map resolve-morph-context (vals grouped))
        generated-families (map-indexed resolve-lex-context grouped)
        lexicon (ccg/build-lexicon
                  {:families (map translate/family->entry (concat initial-families generated-families))
                   :morph    (map translate/morph->entry (concat initial-morph (flatten morphology-context)))
                   :macros   (list)})]
    (grammar-builder lexicon)))
