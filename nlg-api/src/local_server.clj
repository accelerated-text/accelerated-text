(ns local-server
  (:require [org.httpkit.server :as server]
            [clojure.tools.logging :as log]
            [nlg.api.data :as data]
            [nlg.api.lexicon :as lexicon]
            [nlg.api.generate :as generate]
            [nlg.api.blockly-workspace :as workspace]
            [cheshire.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as string])
  (:gen-class)
  (:import (java.net URLDecoder)
           (java.nio.charset Charset)))


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

(defn normalize-req [req path]
  (let [headers (:headers req)
        path-params (:path-params path)
        query-string (query->map (:query-string req))
        body (:body req)
        method (:request-method req)
        normalized {:httpMethod            (upper-case-keyword method)
                    :queryStringParameters query-string
                    :headers               headers
                    :body                  (if (= org.httpkit.BytesInputStream (type body))
                                             (-> body (.bytes) (String.) (decode) (encode))
                                             body)
                    :pathParameters        path-params}
        json-str (generate-string normalized)]
    json-str))

(defonce server (atom nil))

(defn read-os [os]
  (String. (.toByteArray os) (Charset/defaultCharset)))

(defn http-result [raw-resp]
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

(defn parse-path [uri]
  (let [matcher (re-matcher #"(?<namespace>(\/(\w|[-])+))\/?(?<id>((\w|[-])+))?\/?(?<file>((\w+|[-])+\.\w+))?" uri)
        _ (re-find matcher)
        namespace (.group matcher "namespace")
        id (.group matcher "id")
        file (.group matcher "file")]
    {:namespace   namespace
     :path-params (if (nil? file)
                    (if (nil? id)
                      {}
                      {:id id})
                    {:user id
                     :file file})}))

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
      (log/errorf "Encountered error '%s' with request '%s'" (.getMessage e) req)
      (log/errorf "Normalized request -> '%s'" (normalize-req req (parse-path (req :uri))))
      (.printStackTrace e)
      {:status  500
       :headers {"Access-Control-Allow-Origin"  "*"
                 "Access-Control-Allow-Headers" "content-type, *"
                 "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}})))


(defn -main
  [& args]
  (println "Running server on: localhost:8080. Press Ctrl+C to stop")
  (reset! server (server/run-server app {:port 8080})))

