(ns acc-text.nlg.gf.service
  (:require [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(defn compile-request [module instance content]
  (let [request-url (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
        request-content {:module module :instance instance :content content}]
    (log/debugf "Compiling grammar via %s:\n%s\n%s" request-url (get content (str module "Body")) (get content (str module "Lex" instance)))
    (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'"
                request-url (json/write-value-as-string request-content))
    @(client/request {:url     request-url
                      :method  :post
                      :headers {"Content-type" "application/json"}
                      :body    (json/write-value-as-string request-content)})))
