(ns acc-text.nlg.enrich.core
  (:require [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]
            [acc-text.nlg.utils :as utils]))

(defn enrich-request
  [context text]
  (let [request-url (or (System/getenv "ENRICH_ENDPOINT") "http://localhost:8002")
        request-content {:context context :text text}]
    (log/debugf "Enriching text via: %s" request-url)
    (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'"
                request-url (json/write-value-as-string request-content))
    (-> @(client/request {:url     request-url
                          :method  :post
                          :headers {"Content-type" "application/json"}
                          :body    (json/write-value-as-string request-content)})
        (get :body)
        (json/read-value utils/read-mapper))))
