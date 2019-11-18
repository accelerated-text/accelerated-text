(ns acc-text.nlg.gf.generator
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph :as sg]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn compile-request [grammar]
  @(client/request {:url     (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
                    :method  :post
                    :headers {"Content-type" "application/json"}
                    :body    (json/write-value-as-string {:content (reduce str grammar)})}))

(defn generate [grammar]
  (-> grammar
      (compile-request)
      (get :body)
      (json/read-value read-mapper)
      (get :results)
      (sort)
      (dedupe)))
