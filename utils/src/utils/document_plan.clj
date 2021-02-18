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

(def read-mapper (json/object-mapper {:decode-key-fn true :pretty true}))

(defn pprint-semantic-graph [file-path]
  (-> (slurp file-path)
        (json/read-value read-mapper)
        (update :documentPlan #(json/read-value % read-mapper))
        (document-plan->semantic-graph)
        (println)))

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

(defn pprint-document-plan [name]
  (-> (run-query (:graphql-url config)
                 (queries/export-document-plan-query {:name name}))
      :body
      (json/read-value read-mapper)
      (update :documentPlan #(json/read-value % read-mapper))
      :data :documentPlan
      (json/write-value-as-string)
      (println)))

(defn export-all-document-plans [output-dir]
  (let [{:keys [body error]} (run-query (:graphql-url config)
                                        (queries/export-document-plans-query {}))]
    (if error
      (log/errorf "Failed with the error: %s" error)
      (doseq [dp (-> (json/read-value body read-mapper)
                     (update :documentPlan #(json/read-value % read-mapper))
                     :data :documentPlans :items)]
        (->file output-dir dp)))))

(defn -main [action & args]
  (mount/start)
  (case action
    "print-graph"  (apply pprint-semantic-graph args)
    "print-plan"   (apply pprint-document-plan args)
    "export-plans" (export-all-document-plans (or (first args) "../api/resources/document-plans"))))
