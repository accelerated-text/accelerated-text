(ns api.server
  (:gen-class)
  (:require [api.graphql.core :as graphql]
            [api.nlg.generate :as generate]
            [api.utils :as utils]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [jsonista.core :as json]
            [mount.core :refer [defstate] :as mount]
            [org.httpkit.server :as server]
            [ring.middleware.multipart-params :as multipart-params]
            [reitit.ring :as ring])
  (:import (java.io ByteArrayOutputStream)))

(def headers {"Access-Control-Allow-Origin"  "*"
              "Access-Control-Allow-Headers" "content-type, *"
              "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"})

(defn health [_] {:status 200, :body "Ok"})

(defn- http-response [body]
  {:status  200
   :headers (assoc headers "Content-Type" "application/json")
   :body    (json/write-value-as-string body)})

(def routes
  (ring/router
   [["/_graphql" {:post (fn [{:keys [body] :as request}]
                          (-> body
                              (utils/read-json-is)
                              (graphql/handle)
                              (http-response)))}]
    ["/nlg" {:post   (fn [{:keys [body]}] (generate/generate-request body))
             :get    (generate/read-result)
             :delete (generate/delete-result)}]
    ["/health" {:get health}]]))


(defn- normalize-request [{:keys [headers query-string body request-method]} path-params]
  (json/write-value-as-string
    {:httpMethod            (-> request-method (name) (str/upper-case) (keyword))
     :queryStringParameters (utils/query->map query-string)
     :headers               headers
     :body                  (some-> body (utils/read-json-is) (json/write-value-as-string))
     :pathParameters        path-params}))

(defn string-store [item]
  (-> (select-keys item [:filename :content-type])
      (assoc :content (slurp (:stream item)))))

(def multipart-handler
  (multipart-params/wrap-multipart-params identity {:store string-store}))

;; (defn app [{:keys [body uri request-method] :as request}]
;;   (let [{:keys [namespace path-params]} (utils/parse-path uri)]
;;     (if (= request-method :options)
;;       {:status  200
;;        :headers headers}
;;       (try
;;         (case namespace
;;           "/_graphql"
;;           (-> body
;;               (utils/read-json-is)
;;               (graphql/handle)
;;               (http-response))
;;           "/nlg"
;;           (let [is (-> request (normalize-request path-params) (.getBytes) (io/input-stream))
;;                 os (ByteArrayOutputStream.)]
;;             (generate/-handleRequest nil is os nil)
;;             {:status  200
;;              :headers (assoc headers "Content-Type" "application/json")
;;              :body    (-> os
;;                           (utils/read-json-os)
;;                           (get :body))})
;;           "/accelerated-text-data-files"
;;           (let [{params :params} (multipart-handler request)
;;                 id (data-files/store! (get params "file"))]
;;             (http-response {:message "Succesfully uploaded file" :id id}))
;;           {:status 404
;;            :body   (format "ERROR: unsupported URI '%s'" uri)})
;;         (catch Exception e
;;           (log/errorf "Encountered error '%s' with request '%s'"
;;                       (.getMessage e) request)
;;           (.printStackTrace e)
;;           {:status  500
;;            :headers headers})))))

(def app
  (ring/ring-handler routes (ring/create-default-handler)))

(defstate http-server
  :start (let [host (or (System/getenv "ACC_TEXT_API_HOST") "0.0.0.0")
               port (Integer/valueOf ^String (or (System/getenv "ACC_TEXT_API_PORT") "3001"))]
           (log/infof "Running server on: localhost:%s. Press Ctrl+C to stop" port)
           (server/run-server
            app {:port     port
                 :ip       host
                 :max-body Integer/MAX_VALUE}))
  :stop (http-server :timeout 100))



(defn -main [& _]
  (mount/start))
