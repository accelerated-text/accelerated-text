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
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.coercion.schema]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [muuntaja.core :as m]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.spec :as spec]
            [clojure.spec.alpha :as s]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.dev.pretty :as pretty])
  (:import (java.io ByteArrayOutputStream)))

(def headers {"Access-Control-Allow-Origin"  "*"
              "Access-Control-Allow-Headers" "content-type, *"
              "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"})

(defn health [_] {:status 200, :body "Ok"})

(defn- http-response [body]
  {:status  200
   :headers (assoc headers "Content-Type" "application/json")
   :body    (json/write-value-as-string body)})

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

(defn cors-handler [_] {:status 200 :headers headers})

(s/def ::query string?)
(s/def ::variables (s/coll-of string?))
(s/def ::context string?)
(s/def ::graphql-req (s/keys :req-un [::query]
                             :opt-un [::variables ::context]))

(s/def ::documentPlanId string?)
(s/def ::dataId string?)
(s/def ::readerFlagValues (s/coll-of string?))
(s/def ::generate-req (s/keys :req-un [::documentPlanId ::dataId]
                              :opt-un [::readerFlagValues]))

(def routes
  (ring/router
   [["/_graphql" {:post {:parameters {:body ::graphql-req}
                         :handler (fn [{{body :body} :parameters}]
                                    (graphql/handle body))
                         :summary "GraphQL endpoint"}
                  :options cors-handler}]
    ["/nlg/" {:post   {:parameters {:body ::generate-req}
                       :responses {200 {:body {:resultId string?}}}
                       :summary "Registers document plan for generation"
                       :handler (fn [{{body :body} :parameters}]
                                  (generate/generate-request body))}
              :options cors-handler}]
    ["/nlg/:id" {:get     generate/read-result
                 :delete  generate/delete-result}]
    ["/accelerated-text-data-files/" {:options cors-handler
                                      :post {:parameters {:multipart {:file multipart/temp-file-part}}
                                             :responses {200 {:body {:message string?}}}
                                             :summary "Accepts CSV data files from user"
                                             :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                                                        (let [id (data-files/store! file)]
                                                          {:status 200
                                                           :body {:message "Succesfully uploaded file" :id id}}))}}]
    ["/swagger.json"
     {:get {:no-doc true
            :swagger {:info {:title "nlg-api"
                             :description "api description"}}
            :handler (swagger/create-swagger-handler)}}]
    ["/health" {:get health}]]
   {:data {:coercion reitit.coercion.spec/coercion
           :muuntaja m/instance
           :middleware [ ;; swagger feature
                        swagger/swagger-feature
                        ;; query-params & form-params
                        parameters/parameters-middleware
                        ;; content-negotiation
                        muuntaja/format-negotiate-middleware
                        ;; encoding response body
                        muuntaja/format-response-middleware
                        ;; exception handling
                        exception/exception-middleware
                        ;; decoding request body
                        muuntaja/format-request-middleware
                        ;; coercing response bodys
                        coercion/coerce-response-middleware
                        ;; coercing request parameters
                        coercion/coerce-request-middleware
                        ;; multipart
                        multipart/multipart-middleware]}
    :exception pretty/exception}))


(def app
  (ring/ring-handler
   routes
   (swagger-ui/create-swagger-ui-handler
    {:path "/"
     :config {:validatorUrl nil
              :operationsSorter "alpha"}})
   (ring/create-default-handler)))

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

