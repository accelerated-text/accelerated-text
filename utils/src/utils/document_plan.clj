(ns utils.document-plan
  (:gen-class)
  (:require [api.nlg.parser :refer [document-plan->semantic-graph]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [mount.core :as mount]
            [utils.config :refer [config]]
            [utils.queries :as queries]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(def write-mapper (json/object-mapper {:pretty true}))

(defn pprint-semantic-graph [file-path]
  (let [{body :documentPlan :as document-plan} (json/read-value (slurp file-path) read-mapper)]
    (-> document-plan
        (cond-> (string? body) (update :documentPlan #(json/read-value % read-mapper)))
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
          (json/write-value-as-string document-plan write-mapper))))

(defn pprint-document-plan [name]
  (-> (queries/run-query (format "%s/_graphql" (:api-url config))
                         (queries/export-document-plan-query {:name name}))
      :body
      (json/read-value read-mapper)
      :data :documentPlan
      (update :documentPlan #(json/read-value % read-mapper))
      (json/write-value-as-string write-mapper)
      (println)))

(defn export-all-document-plans [output-dir]
  (let [{:keys [body error]} (queries/run-query (format "%s/_graphql" (:api-url config))
                                                (queries/export-document-plans-query {}))]
    (if error
      (log/errorf "Failed with the error: %s" error)
      (doseq [dp (-> (json/read-value body read-mapper)
                     :data :documentPlans :items)]
        (->file output-dir (update dp :documentPlan #(json/read-value % read-mapper)))))))

(defn -main [action & args]
  (mount/start)
  (case action
    "print-graph" (apply pprint-semantic-graph args)
    "print-plan" (apply pprint-document-plan args)
    "export-plans" (export-all-document-plans (or (first args) "../api/resources/document-plans"))))
