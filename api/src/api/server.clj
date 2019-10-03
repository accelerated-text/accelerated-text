(ns api.server
  (:gen-class)
  (:require [api.graphql.core :as graphql]
            [api.nlg.generate :as generate]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clojure.walk :as walk]
            [org.httpkit.server :as server])
  (:import (java.net URLDecoder)
           (java.nio.charset Charset)
           (java.io ByteArrayOutputStream)
           (org.httpkit BytesInputStream)))

(defonce server (atom nil))

(def headers {"Access-Control-Allow-Origin"  "*"
              "Access-Control-Allow-Headers" "content-type, *"
              "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"})

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn- split-param [param]
  (take 2 (-> (str/split param #"=")
              (concat (repeat "")))))

(defn- url-decode
  ([string]
   (url-decode string "UTF-8"))
  ([string encoding]
   (when (some? string)
     (URLDecoder/decode string encoding))))

(defn- query->map [query-string]
  (when-not (str/blank? query-string)
    (some->> (str/split query-string #"&")
             (seq)
             (mapcat split-param)
             (map url-decode)
             (apply hash-map)
             (walk/keywordize-keys))))

(defn- normalize-body [body]
  (-> body
      (.bytes)
      (String.)
      (json/decode true)))

(defn- normalize-request [{:keys [headers query-string body request-method]} path-params]
  (json/generate-string
    {:httpMethod            (-> request-method (name) (str/upper-case) (keyword))
     :queryStringParameters (query->map query-string)
     :headers               headers
     :body                  (cond-> body
                                    (= BytesInputStream (type body)) (-> (normalize-body) (json/encode)))
     :pathParameters        path-params}))

(defn- parse-path [uri]
  (let [matcher (re-matcher #"(?<namespace>(\/(\w|[-])+))\/?(?<id>((\w|[-])+))?\/?(?<file>((\w+|[-])+\.\w+))?" uri)
        _ (re-find matcher)
        namespace (.group matcher "namespace")
        id (.group matcher "id")
        file (.group matcher "file")]
    {:namespace   (str/lower-case namespace)
     :path-params (cond
                    (some? file) {:user id :file file}
                    (some? id) {:id id}
                    :else {})}))

(defn- http-result [body]
  {:status  200
   :headers (assoc headers "Content-Type" "application/json")
   :body    body})

(defn app [{:keys [body uri request-method] :as request}]
  (let [{:keys [namespace path-params]} (parse-path uri)
        normalized-req (normalize-request request path-params)]
    (if (= request-method :options)
      {:status  200
       :headers headers}
      (try
        (case namespace
          "/_graphql" (-> body
                          (normalize-body)
                          (graphql/nlg)
                          (http-result)
                          (update :body json/encode))
          "/nlg" (let [is (io/input-stream (.getBytes normalized-req))
                       os (ByteArrayOutputStream.)]
                   (generate/-handleRequest nil is os nil)
                   (-> os
                       (.toByteArray)
                       (String. (Charset/defaultCharset))
                       (json/decode true)
                       (get :body)
                       (http-result)))
          {:status 404
           :body   (format "ERROR: unsupported URI '%s'" uri)})
        (catch Exception e
          (log/errorf "Encountered error '%s' with request '%s'" (.getMessage e) request)
          (log/errorf "Normalized request -> '%s'" normalized-req)
          (.printStackTrace e)
          {:status  500
           :headers headers})))))

(defn -main [& _]
  (let [host (or (System/getenv "ACC_TEXT_API_HOST") "0.0.0.0")
        port (Integer/valueOf ^String (or (System/getenv "ACC_TEXT_API_PORT") "3001"))]
    (log/infof "Running server on: localhost:%s. Press Ctrl+C to stop" port)
    (reset! server (server/run-server #'app {:port port :ip host}))))
