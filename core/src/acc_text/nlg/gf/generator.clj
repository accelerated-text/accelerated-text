(ns acc-text.nlg.gf.generator
  (:require [jsonista.core :as json]
            [org.httpkit.client :as client]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn compile-request [name abstract concrete]
  @(client/request {:url     (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
                    :method  :post
                    :headers {"Content-type" "application/json"}
                    :body    (json/write-value-as-string {:name name
                                                          :abstract {:content (apply str abstract)}
                                                          :concrete (map
                                                                     (fn [[idx c]] {:key idx
                                                                                    :content (apply str c)})
                                                                     concrete)})}))


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
