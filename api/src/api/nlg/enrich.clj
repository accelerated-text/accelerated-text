(ns api.nlg.enrich
  (:require [api.utils :as utils]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(defn enrich-endpoint []
  (or (System/getenv "ENRICH_ENDPOINT") "http://localhost:8002"))

(defn enrich-request [content]
  (log/debugf "Enriching text via: %s" (enrich-endpoint))
  (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'"
              (enrich-endpoint) (json/write-value-as-string content))
  (-> @(client/request {:url     (enrich-endpoint)
                        :method  :post
                        :headers {"Content-type" "application/json"}
                        :body    (json/write-value-as-string content)})
      (get :body)
      (json/read-value utils/read-mapper)
      (get :results)))

(defn enrich-texts [texts data]
  (log/debugf "Texsts for enrich: %s" texts)
  (enrich-request {:texts    texts
                   :context (reduce-kv (fn [m k v]
                                         (assoc m v (format "{%s}" (name k))))
                                       {}
                                       data)}))
