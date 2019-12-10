(ns api.server
  (:gen-class)
  (:require [api.config :refer [conf]]
            [api.graphql.core :as graphql]
            [api.nlg.generate :as generate]
            [api.utils :as utils]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
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
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.dev.pretty :as pretty]
            [data.entities.amr :as amr]))

(def headers {"Access-Control-Allow-Origin"  "*"
              "Access-Control-Allow-Headers" "content-type, *"
              "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
              "Content-Type"                 "application/json"})

(defn health [_] {:status 200, :body "Ok"})

(defn string-store [item]
  (-> (select-keys item [:filename :content-type])
      (assoc :content (slurp (:stream item)))))

(def multipart-handler
  (multipart-params/wrap-multipart-params identity {:store string-store}))

(defn cors-handler [_] {:status 200 :headers headers})

(defn wrap-response [handler]
  (fn [request]
    (let [resp (handler request)]
      (assoc resp :headers (merge (:headers resp) headers)))))

(def routes
  (ring/router
   [["/_graphql"    {:post {:handler (fn [{raw :body}]
                                       (let [body (utils/read-json-is raw)]
                                         {:status 200
                                          :body (graphql/handle body)}))
                            :summary "GraphQL endpoint"}
                     :options cors-handler}]
    ["/nlg/"        {:post   {:parameters {:body ::generate/generate-req}
                              :responses  {200 {:body {:resultId string?}}}
                              :summary    "Registers document plan for generation"
                              :coercion   reitit.coercion.spec/coercion
                              :middleware [muuntaja/format-request-middleware
                                           coercion/coerce-request-middleware
                                           coercion/coerce-response-middleware]
                              :handler (fn [{{body :body} :parameters}]
                                         (generate/generate-request body))}
                     :options cors-handler}]
    ["/nlg/_bulk/"   {:post    {:parameters {:body ::generate/generate-bulk}
                                :responses  {200 {:body {:resultId string?}}}
                                :summary    "Bulk generation"
                                :coercion   reitit.coercion.spec/coercion
                                :middleware [muuntaja/format-request-middleware
                                             coercion/coerce-request-middleware
                                             coercion/coerce-response-middleware]
                                :handler (fn [{{body :body} :parameters}]
                                           (generate/generate-bulk body))}
                     :options cors-handler}]
    ["/nlg/:id"     {:get     {:parameters {:query ::generate/format-query
                                            :path  {:id string?}}
                               :coercion   reitit.coercion.spec/coercion
                               :summary    "Get NLG result"
                               :middleware [muuntaja/format-request-middleware
                                            coercion/coerce-request-middleware]
                               :handler    generate/read-result}
                     :delete  generate/delete-result
                     :options cors-handler}]
    ["/accelerated-text-data-files/" {:post (fn [request]
                                              (let [{params :params} (multipart-handler request)
                                                    id (data-files/store! (get params "file"))]
                                                {:status 200
                                                 :body {:message "Succesfully uploaded file" :id id}}))}]
    ["/swagger.json" {:get {:no-doc true
                            :swagger {:info {:title "nlg-api"
                                             :description "api description"}}
                            :handler (swagger/create-swagger-handler)}}]
    ["/health"       {:get health}]]
   {:data {
           :muuntaja m/instance
           :middleware [swagger/swagger-feature
                        muuntaja/format-negotiate-middleware
                        parameters/parameters-middleware
                        wrap-response
                        muuntaja/format-response-middleware
                        exception/exception-middleware]}
    :exception pretty/exception}))

(def app
  (ring/ring-handler
    routes
    (swagger-ui/create-swagger-ui-handler
      {:path   "/"
       :config {:validatorUrl     nil
                :operationsSorter "alpha"}})
    (ring/create-default-handler)))

(defn start-http-server [conf]
  (let [host (get conf :host "0.0.0.0")
        port (get conf :port 3001)]
    (log/infof "Running server on: localhost:%s. Press Ctrl+C to stop" port)
    (amr/initialize)
    (server/run-server
      #'app {:port     port
             :ip       host
             :max-body Integer/MAX_VALUE})))

(defstate http-server
          :start (start-http-server conf)
          :stop (http-server :timeout 100))

(defn -main [& _]
  (mount/start))
