(ns api.nlg.enrich
  (:require [api.utils :as utils]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as http]))

(defn enable-enrich? []
  (Boolean/valueOf (System/getenv "ENABLE_ENRICH")))

(defn enrich-endpoint []
  (or (System/getenv "ENRICH_ENDPOINT") "http://localhost:8002"))

(defn enrich-request [content]
  (log/infof "Enriching text via: %s" (enrich-endpoint))
  (let [{{:keys [results error message]} :body request-error :error}
        (-> @(http/request {:url     (enrich-endpoint)
                            :method  :post
                            :headers {"Content-type" "application/json"}
                            :body    (json/write-value-as-string content)})
            (update :body #(json/read-value % utils/read-mapper)))]
    (cond
      (true? error) (log/errorf "Failed to enrich text: %s" message)
      (some? request-error) (log/errorf "Enrich request failure: %s" (.getMessage request-error))
      :else results)))

(defn build-context [data]
  (reduce-kv (fn [m k v]
               (assoc m v (format "{%s}" (name k))))
             {}
             data))

(defn enrich [gen-result data]
  (mapcat (fn [{:keys [enriched] :as result}]
            (if (some? enriched)
              (let [item          (dissoc result :enriched)
                    enriched-item (assoc item :text enriched :enriched? true)]
                [item enriched-item])
              [result]))
          (enrich-request {:data gen-result :context (build-context data)})))
