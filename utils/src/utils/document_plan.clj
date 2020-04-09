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

(defn ouput-document-plan [dir dp])

(defn ->file [output-dir {:keys [id] :as document-plan}]
  (let [fpath (format "%s/%s.json" output-dir id)]
    (log/debugf "Writing to: %s" fpath)
    (spit
     fpath
     (json/write-value-as-string document-plan))))

(defn export-document-plan [name]
  (let [{:keys [graphql-url]} config
        {:keys [status body error]} @(http/post graphql-url {:headers {"Content-Type" "application/json"}
                                                             :body (->> (queries/export-document-plan-query {:name name})
                                                                        (log/spyf "Query Content: %s")
                                                                        :graphql
                                                                        (json/write-value-as-string))})]
    (-> (json/read-value body read-mapper) :data :documentPlan (json/write-value-as-string) (pprint))))

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
       (log/errorf "Failed, exception is: %s" error)
       (doseq [dp (-> (json/read-value body read-mapper) :data :documentPlans :items)]
         (->file output-dir dp))))))

(defn -main [& args]
  (mount/start)
  (println queries/query-map)
  (let [[action & other] args]
    (case action
      "to-semantic-graph" (apply ->semantic-graph other)
      "export" (apply export-document-plan other)
      "export-all" (apply export-all-document-plans other))))
