(ns utils.document-plan
  (:gen-class)
  (:require [api.nlg.parser :refer [document-plan->semantic-graph]]
            [jsonista.core :as json]
            [clojure.pprint :refer [pprint]]))


(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn ->semantic-graph [file-path]
  (-> (slurp file-path)
      (json/read-value read-mapper)
      :documentPlan
      (document-plan->semantic-graph)
      (pprint)))

(defn export-document-plan [name])

(defn export-all-document-plans
  ([] (export-all-document-plans "api/resources/document-plans"))
  ([output-dir] ()))

(defn -main [& args]
  (let [[action & other] args]
    (case action
      "to-semantic-graph" (apply ->semantic-graph other)
      "export" (apply export-document-plan other)
      "export-all" (apply export-all-document-plans other))))
