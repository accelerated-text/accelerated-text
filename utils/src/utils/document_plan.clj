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

(defn -main [& args]
  (let [[action & other] args]
    (case action
      "to-semantic-graph" (apply ->semantic-graph other))))
