#!/bin/sh

"exec" "clj" "-Sdeps" "{:deps,{http-kit,{:mvn/version\"2.3.0\"},lt.tokenmill.nlg,{:local/root\"target/uberjar/nlg-api-0.1.0-SNAPSHOT-standalone.jar\"}}}" "$0" "$@"

(ns local-server
  (:require [org.httpkit.server :as server])
  (:import (lt.tokenmill.nlg.api DataHandler)))

(defn normalize-req [req] req)

(def dataHandler (new DataHandler))

(defn app [req]
  (let [path (req :uri)]
    (case path
      "/data" (.handleRequest dataHandler (normalize-req req)))))

(println "Running server on: localhost:8080. Press Ctrl+C to stop")
(server/run-server app {:port 8080})
