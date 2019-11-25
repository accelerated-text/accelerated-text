(ns acc-text.nlg.gf.generator
  (:require [jsonista.core :as json]
            [org.httpkit.client :as client]
            [clojure.tools.logging :as log]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn compile-request [name abstract concrete]
  (let [request-url (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
        request-content {:name name
                         :abstract {:content (apply str abstract)}
                         :concrete (map
                                    (fn [[idx c]] {:key idx
                                                   :content (apply str c)})
                                    concrete)}]
    (log/debugf "Compiling grammar via %s:\n%s\n%s" request-url abstract (-> concrete (first) (second)))
    (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'" request-url (json/write-value-as-string request-content))
    @(client/request {:url     request-url
                      :method  :post
                      :headers {"Content-type" "application/json"}
                      :body    (json/write-value-as-string request-content)})))


(defn flatten-results [results]
  ;; {:results [(key1, array1), (key2, array2)]} -> array
  (flatten
   (map (fn [[_ r]] r) results)))

(defn generate [name abstract concrete]
  (-> (compile-request name abstract concrete)
      (get :body)
      (json/read-value read-mapper)
      (get :results)
      (flatten-results)
      (sort)
      (dedupe)))
