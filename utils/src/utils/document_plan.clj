(ns utils.document-plan
  (:gen-class)
  (:require [api.nlg.parser :refer [document-plan->semantic-graph]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [mount.core :as mount]
            [org.httpkit.client :as http]
            [utils.config :refer [config]]
            [utils.queries :as queries]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn ->semantic-graph [file-path]
  (-> (slurp file-path)
      (json/read-value read-mapper)
      :documentPlan
      (document-plan->semantic-graph)
      (pprint)))

(defn ->file [output-dir {:keys [id] :as document-plan}]
  (spit (format "%s/%s.json" output-dir id)
        (json/write-value-as-string document-plan)))

(defn export-document-plan [name]
  (let [{:keys [graphql-url]} config
        {:keys [status body error]} @(http/post graphql-url {:headers {"Content-Type" "application/json"}
                                                             :body (->> (queries/export-document-plan-query {:name name})
                                                                        :graphql
                                                                        (json/write-value-as-string))})]
    (-> (json/read-value body read-mapper) :data :documentPlan (json/write-value-as-string) (pprint))))

(defn doc->dir-name [{kind :kind}]
  (condp = kind
    "RGL" "dlg" "Document" "dp" "AMR" "amr"))

(defn export-all-document-plans
  ([] (export-all-document-plans "../api/resources/document-plans"))
  ([output-dir]
   (let [{:keys [graphql-url]} config
         {:keys [body error]}  @(http/post graphql-url {:headers {"Content-Type" "application/json"}
                                                        :body    (->> (queries/export-document-plans-query {})
                                                                      :graphql
                                                                      (json/write-value-as-string))})]
     (if error
       (log/errorf "Failed, exception is: %s" error)
       (doseq [dp (-> (json/read-value body read-mapper) :data :documentPlans :items)]
         (->file (str output-dir "/" (doc->dir-name dp)) dp))))))

(defn -main [& args]
  (mount/start)
  (println queries/query-map)
  (let [[action & other] args]
    (case action
      "to-semantic-graph" (apply ->semantic-graph other)
      "export" (apply export-document-plan other)
      "export-all" (apply export-all-document-plans other))))
