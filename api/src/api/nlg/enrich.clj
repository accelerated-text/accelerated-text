(ns api.nlg.enrich
  (:require [api.utils :as utils]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(defn enable-enrich? []
  (Boolean/valueOf ^String (System/getenv "ENABLE_ENRICH")))

(defn enrich-endpoint []
  (or (System/getenv "ENRICH_ENDPOINT") "http://localhost:8002"))

(defn enrich-request [content]
  (log/debugf "Enriching text via: %s" (enrich-endpoint))
  (let [{{:keys [result error message]} :body request-error :error}
        (-> @(client/request {:url     (enrich-endpoint)
                              :method  :post
                              :headers {"Content-type" "application/json"}
                              :body    (json/write-value-as-string content)})
            (update :body #(json/read-value % utils/read-mapper)))]
    (cond
      (true? error) (log/errorf "Failed to enrich text: %s" message)
      (some? request-error) (log/errorf "Enrich request failure: %s" (.getMessage request-error))
      :else result)))

(defn enrich-text [text data]
  (enrich-request {:text    text
                   :context (reduce-kv (fn [m k v]
                                         (assoc m v (format "{%s}" (name k))))
                                       {}
                                       data)}))
