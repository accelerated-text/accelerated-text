(ns api.nlg.generator.grammar
  (:require [acc-text.nlg.grammar :as ccg]
            [acc-text.nlg.dsl.core :as dsl]
            [acc-text.nlg.ccg.base-en :as base-en]
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


(defn compile-custom-grammar
  [values]
  (log/debugf "Dynamic values: %s" (pr-str values))
  (let [grammar-builder (ccg/build-grammar
                         {:types (ccg/build-types [{:name "sem-obj"}
                                                   {:name "phys-obj" :parents "sem-obj"}])
                          :rules (ccg/build-default-rules)})
        grouped            (group-by (fn [item] (get-in item [:attrs :type])) values)
        morphology-context (map resolve-morph-context (vals grouped))
        generated-families (map-indexed resolve-lex-context grouped)
        lexicon            (ccg/build-lexicon
                            {:families (map translate/family->entry (concat base-en/initial-families generated-families))
                             :morph    (map translate/morph->entry (concat base-en/initial-morph (flatten morphology-context)))
                             :macros   []})]
    (grammar-builder lexicon)))
