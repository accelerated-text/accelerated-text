(ns local-server
  (:require [org.httpkit.server :as server]
            [lt.tokenmill.nlg.api.data :as data]
            [cheshire.core :refer :all]
            [clojure.java.io :as io])
  (:gen-class))

(defn normalize-req
  [req]
  (let [json-str (generate-string req)]
    json-str))

(defn app [req]
  (let [path (req :uri)
        is (io/input-stream (.getBytes (normalize-req req)))
        os (java.io.ByteArrayOutputStream.)]
    (case path
      "/data" (data/-handleRequest nil is os nil))
    (-> (slurp os)
        decode)))


(defn -main
  [& args]
  (println "Running server on: localhost:8080. Press Ctrl+C to stop")
  (server/run-server app {:port 8080}))

