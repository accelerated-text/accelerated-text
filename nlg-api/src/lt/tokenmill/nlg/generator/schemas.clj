(ns lt.tokenmill.nlg.generator.schemas
  (:require [schema.core :as s]
            [lt.tokenmill.nlg.generator.templates :as templates]))


(def product-schema-1
  {:product-name s/Str
   :relation s/Str
   :adverb s/Str
   :features [s/Str]})

(def product-schema-2
  {:product-name s/Str
   :relation s/Str
   :adverb s/Str
   :features [s/Str]
   :elaborate s/Str})


(def available-templates [{:schema product-schema-1 :fn templates/product-1}
                          {:schema product-schema-2 :fn templates/product-2}])

(defn check-template
  [template data]
  (try
    (do
      (s/validate (template :schema) data)
      true)
    (catch Exception e (println e)  false)))

(defn matching-templates
  [args]
  (filter #(check-template % args) available-templates))
