(ns acc-text.nlg.gf.service
  (:require [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(defn compile [module abstract-grammar concrete-grammar]
  (let [request-url (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
        request-content {:name     (name module)
                         :abstract {:content abstract-grammar}
                         :concrete [{:key     1
                                     :content concrete-grammar}]}]
    (log/debugf "Compiling grammar via %s:\n%s\n%s" request-url abstract-grammar concrete-grammar)
    (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'"
                request-url (json/write-value-as-string request-content))
    @(client/request {:url     request-url
                      :method  :post
                      :headers {"Content-type" "application/json"}
                      :body    (json/write-value-as-string request-content)})))
