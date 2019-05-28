(ns local-server
  (:require [org.httpkit.server :as server]
            [clojure.tools.logging :as log]
            [lt.tokenmill.nlg.api.data :as data]
            [lt.tokenmill.nlg.api.lexicon :as lexicon]
            [lt.tokenmill.nlg.api.generate :as generate]
            [lt.tokenmill.nlg.api.blockly-workspace :as workspace]
            [cheshire.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as string])
  (:gen-class)
  (:import (java.net URLDecoder)))


(defn upper-case-keyword
  [key]
  (-> (name key)
      (clojure.string/upper-case)
      (keyword)))

(defn split-param [param]
  (-> (string/split param #"=")
      (concat (repeat ""))
      (->>
        (take 2))))

(defn url-decode
  ([string] (url-decode string "UTF-8"))
  ([string encoding]
   (some-> string str (URLDecoder/decode encoding))))

(defn query->map [qstr]
  (when (not (string/blank? qstr))
    (some->> (string/split qstr #"&")
             seq
             (mapcat split-param)
             (map url-decode)
             (apply hash-map)
             (clojure.walk/keywordize-keys))))

(defn normalize-req
  [req path]
  (println req)
  (let [headers (:headers req)
        path-params (:path-params path)
        query-string (query->map (:query-string req))
        body (:body req)
        method (:request-method req)
        normalized {:httpMethod            (upper-case-keyword method)
                    :queryStringParameters query-string
                    :headers               headers
                    :body                  body
                    :pathParameters        path-params}
        json-str (generate-string normalized)]
    json-str))

(defonce server (atom nil))

(defn read-os
  [os]
  (String. (.toByteArray os) (. java.nio.charset.Charset defaultCharset)))

(defn http-result
  [raw-resp]
  (let [resp (decode raw-resp true)
        body (:body resp)]
    (println resp)
    (println body)
    {:status  200
     :headers {"Access-Control-Allow-Origin"  "*"
               "Access-Control-Allow-Headers" "content-type, *"
               "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
               "Content-Type"                 "application/json"}
     :body    body}))

(defn parse-path
  [uri]
  (let [matcher (re-matcher #"(?<namespace>(/(\w|[-])+))/?(?<id>((\w|[-])+))?/?" uri)
        _ (re-find matcher)
        namespace (.group matcher "namespace")
        id (.group matcher "id")]
    {:namespace   namespace
     :path-params (if (nil? id)
                    {}
                    {:id id})}))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn app [req]
  (try
    (let [path (parse-path (req :uri))
          is (io/input-stream (.getBytes (normalize-req req path)))
          os (java.io.ByteArrayOutputStream.)]
      (case (:namespace path)
        "/data" (data/-handleRequest nil is os nil)
        "/lexicon" (lexicon/-handleRequest nil is os nil)
        "/nlg" (generate/-handleRequest nil is os nil)
        "/document-plans" (workspace/-handleRequest nil is os nil))
      (-> (read-os os)
          (http-result)))
    (catch Exception e
      (log/errorf "Encountered error '%s' with request '%s' \n %s"
                  (.getMessage e) req (.printStackTrace e))
      {:status  500
       :headers {"Access-Control-Allow-Origin"  "*"
                 "Access-Control-Allow-Headers" "content-type, *"
                 "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}})))


(defn -main
  [& args]
  (println "Running server on: localhost:8080. Press Ctrl+C to stop")
  (reset! server (server/run-server app {:port 8080})))

