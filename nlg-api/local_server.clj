#!/bin/sh

"exec" "clj" "-Sdeps" "{:deps,{http-kit,{:mvn/version\"2.3.0\"}}}" "$0" "$@"

(ns local-server
  (:gen-class)
  (:require [org.httpkit.server :as server]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(println "Running server on: localhost:8080. Press Ctrl+C to stop")
(server/run-server app {:port 8080})
