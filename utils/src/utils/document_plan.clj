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

(defn pprint-semantic-graph [file-path]
  (let [dp       (json/read-value (slurp file-path) read-mapper)
        fixed-dp (assoc dp
                        :documentPlan
                        (json/read-value (:documentPlan dp) read-mapper))]
    (-> fixed-dp
        (document-plan->semantic-graph)
        (pprint))))

(defn doc->dir-name [{kind :kind}]
  (condp = kind
    "RGL" "dlg" "Document" "dp" "AMR" "amr"))

(defn ->file [output-dir {:keys [id] :as document-plan}]
  (let [dir (str output-dir "/" (doc->dir-name document-plan))]
    (.mkdirs (io/file dir))
    (log/infof "Writing: %s/%s.json" dir id)
    (spit (format "%s/%s.json" dir id)
          (json/write-value-as-string document-plan))))

(defn run-query [url q]
  @(http/post url {:headers {"Content-Type" "application/json"}
                   :body    (->> q
                                 :graphql
                                 (json/write-value-as-string))}))

(defn export-document-plan [name]
  (-> (run-query (:graphql-url config)
                 (queries/export-document-plan-query {:name name}))
      :body
      (json/read-value read-mapper)
      :data :documentPlan
      (json/write-value-as-string)
      (pprint)))

(defn export-all-document-plans
  ([] (export-all-document-plans "../api/resources/document-plans"))
  ([output-dir]
   (let [{:keys [body error]} (run-query (:graphql-url config)
                                         (queries/export-document-plans-query {}))]
     (if error
       (log/errorf "Failed with the error: %s" error)
       (doseq [dp (-> (json/read-value body read-mapper) :data :documentPlans :items)]
         (->file output-dir dp))))))

(defn -main [action & args]
  (mount/start)
  (case action
      "graph" (apply pprint-semantic-graph args)
      "plan"  (apply export-document-plan args)
      "all-plans"   (export-all-document-plans)))
