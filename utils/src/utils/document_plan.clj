(ns utils.document-plan
  (:gen-class)
  (:require [api.nlg.parser :refer [document-plan->semantic-graph]]
            [jsonista.core :as json]
            [clojure.pprint :refer [pprint]]
            [utils.queries :as queries]
            [mount.core :refer [defstate] :as mount]
            [utils.config :refer [config]]
            [clojure.tools.logging :as log]
            [org.httpkit.client :as http]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn ->semantic-graph [file-path]
  (-> (slurp file-path)
      (json/read-value read-mapper)
      :documentPlan
      (document-plan->semantic-graph)
      (pprint)))

(defn export-document-plan [name])

(defn export-all-document-plans
  ([] (export-all-document-plans "../api/resources/document-plans"))
  ([output-dir]
   (let [{:keys [graphql-url]} config
         {:keys [status body error]} @(http/post graphql-url {:headers {"Content-Type" "application/json"}
                                                              :body (->> (queries/export-document-plans-query {})
                                                                         (log/spyf "Query Content: %s")
                                                                         :graphql
                                                                         (json/write-value-as-string))})]
     (if error
       (println "Failed, exception is: " error)
       (pprint body)))))

(defn -main [& args]
  (mount/start)
  (let [[action & other] args]
    (case action
      "to-semantic-graph" (apply ->semantic-graph other)
      "export" (apply export-document-plan other)
      "export-all" (apply export-all-document-plans other))))
