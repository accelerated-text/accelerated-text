(ns acc-text.nlg.gf.service
  (:require [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(defn compile-request [lang module instance content]
  (let [request-url (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
        request-content {:module module :instance instance :content content}]
    (log/debugf "Compiling grammar via %s" request-url)
    (log/debugf "** Abstract **\n%s\n" (get content module))
    (log/debugf "** Incomplete concrete **\n%s\n" (get content (str module "Body")))
    (log/debugf "** Concrete **\n%s" (get content (str module instance)))
    (log/debugf "** Lex interface **\n%s\n" (get content (str module "Lex")))
    (log/debugf "** Lex data **\n%s\n" (get content (str module "Lex" lang)))
    (log/debugf "** Ops **\n%s\n" (get content (str module "Ops")))
    (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'"
                request-url (json/write-value-as-string request-content))
    @(client/request {:url     request-url
                      :method  :post
                      :headers {"Content-type" "application/json"}
                      :body    (json/write-value-as-string request-content)})))
