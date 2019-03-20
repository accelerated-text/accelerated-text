(ns local-server
  (:require [org.httpkit.server :as server]
            [clojure.tools.logging :as log]
            [lt.tokenmill.nlg.api.data :as data]
            [lt.tokenmill.nlg.api.lexicon :as lexicon]
            [cheshire.core :refer :all]
            [clojure.java.io :as io])
  (:gen-class))


(defn upper-case-keyword
  [key]
  (-> (name key)
      (clojure.string/upper-case)
      (keyword)))

(defn normalize-req
  [req path]
  (println req)
  (let [headers (:headers req)
        path-params (:path-params path)
        query-string (:query-string req)
        body (:body req)
        method (:request-method req)
        normalized {:httpMethod (upper-case-keyword method)
                    :queryStringParameters query-string
                    :headers headers
                    :body body
                    :pathParams path-params}
        json-str (generate-string normalized)]
    json-str))

(defonce server (atom nil))

(defn read-os
  [os]
  (String. (.toByteArray os) (. java.nio.charset.Charset defaultCharset)))

(defn http-result
  [raw-resp]
  (let [resp (decode raw-resp)
        body (:body resp)]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (generate-string body)}))

(defn parse-path
  [uri]
  (let [matcher (re-matcher #"(?<namespace>(/\w+))/?(?<id>(\w+))?/?" uri)
        _ (re-find matcher)
        namespace (.group matcher "namespace")
        id (.group matcher "id")]
    {:namespace namespace
     :path-params {:id id}}))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn app [req]
  (let [path (parse-path (req :uri))
        is (io/input-stream (.getBytes (normalize-req req path)))
        os (java.io.ByteArrayOutputStream.)]
    (case (:namespace path)
      "/data" (data/-handleRequest nil is os nil))
    
    (-> (read-os os)
        (http-result))))


(defn -main
  [& args]
  (println "Running server on: localhost:8080. Press Ctrl+C to stop")
  (reset! server (server/run-server app {:port 8080})))

