(ns acc-text.nlg.gf.service
  (:require [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(def write-mapper (json/object-mapper {:escape-non-ascii true}))

(defn request
  ([payload lang]
   (request "Default" "Instance" payload lang))
  ([module instance payload lang]
   (let [request-url (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
         request-content {:module module :instance instance :content payload}]
     (log/debugf "Compiling grammar via %s" request-url)
     (log/debugf "** Abstract **\n%s\n" (get payload module))
     (log/debugf "** Incomplete concrete **\n%s\n" (get payload (str module "Body")))
     (log/debugf "** Concrete **\n%s" (get payload (str module instance)))
     (log/debugf "** Lex interface **\n%s\n" (get payload (str module "Lex")))
     (log/debugf "** Lex data **\n%s\n" (get payload (str module "Lex" lang)))
     (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'"
                 request-url (json/write-value-as-string request-content write-mapper))
     (let [{body :body request-error :error}
           @(client/request {:url     request-url
                             :method  :post
                             :headers {"Content-type" "application/json"}
                             :body    (json/write-value-as-string request-content write-mapper)})
           {[[_ results]] :results error :error} (json/read-value body read-mapper)]
       (cond
         (some? request-error) (throw request-error)
         (some? error) (throw (Exception. ^String error))
         :else (distinct results))))))
