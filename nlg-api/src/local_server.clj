(ns local-server
  (:require [org.httpkit.server :as server]
            [lt.tokenmill.nlg.api.data :as data])
  (:gen-class))

(defn normalize-req [req] req)

(defn app [req]
  (let [path (req :uri)]
    (case path
      "/data" (data/-handleRequest (normalize-req req)))))


(defn -main
  [& args]
  (println "Running server on: localhost:8080. Press Ctrl+C to stop")
  (server/run-server app {:port 8080}))

