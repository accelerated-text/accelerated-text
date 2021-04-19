(ns api.server
  (:gen-class)
  (:require [api.config :refer [conf]]
            [api.graphql.core :as graphql]
            [api.nlg.service :as service]
            [api.resource :refer [response-examples]]
            [api.utils :as utils]
            [api.error :as errors]
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
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.dev.pretty :as pretty]
            [acc-text.nlg.gf.service :as gf-service]))

(def headers {"Access-Control-Allow-Origin"  "*"
              "Access-Control-Allow-Headers" "content-type, *"
              "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
              "Content-Type"                 "application/json"})

(defn health [_] {:status 200, :body {:health "Ok"}})

(defn status [_]
  (let [main-deps {:gf (gf-service/ping)}
        other-deps {}
        color (cond
                (some false? (vals main-deps)) :red
                (some false? (vals other-deps)) :yellow
                :else :green)]
    {:status 200 :body {:color (name color) :services (merge main-deps other-deps)}}))

(defn string-store [item]
  (-> (select-keys item [:filename :content-type])
      (assoc :content (utils/slurp-bytes (:stream item)))))

(def multipart-handler
  (multipart-params/wrap-multipart-params identity {:store string-store}))

(defn cors-handler [_] {:status 200 :headers headers})

(defn wrap-response [handler]
  (fn [request]
    (let [resp (handler request)]
      (assoc resp :headers (merge (:headers resp) headers)))))

(def routes
  (ring/router
    [["/_graphql" {:post    {:handler (fn [{raw :body}]
                                        (let [body (utils/read-json-is raw)]
                                          {:status 200
                                           :body   (graphql/handle body)}))
                             :summary "GraphQL endpoint"}
                   :options {:handler cors-handler
                             :no-doc  true}}]
     ["/nlg/" {:post    {:parameters {:body ::service/generate-request}
                         :responses  {200 {:body     ::service/generate-response
                                           :examples (get-in response-examples [:nlg :get])}}
                         :summary    "Registers document plan for generation"
                         :coercion   reitit.coercion.spec/coercion
                         :middleware [muuntaja/format-request-middleware
                                      coercion/coerce-request-middleware
                                      coercion/coerce-response-middleware]
                         :handler    (fn [{{body :body} :parameters}]
                                       (service/generate-request body))}
               :options {:handler cors-handler
                         :no-doc  true}}]
     ["/nlg/_bulk/" {:post    {:parameters {:body ::service/generate-request-bulk}
                               :responses  {200 {:body     ::service/generate-response-bulk
                                                 :examples (get-in response-examples [:nlg-bulk :post])}}
                               :summary    "Bulk generation"
                               :coercion   reitit.coercion.spec/coercion
                               :middleware [muuntaja/format-request-middleware
                                            coercion/coerce-request-middleware
                                            coercion/coerce-response-middleware]
                               :handler    (fn [{{body :body} :parameters}]
                                             (service/generate-request-bulk body))}
                     :options {:handler cors-handler
                               :no-doc  true}}]
     ["/nlg/:id" {:get     {:parameters {:query ::service/get-result
                                         :path  {:id string?}}
                            :responses  {200 {:body     ::service/generate-response
                                              :examples (get-in response-examples [:nlg :get])}}
                            :coercion   reitit.coercion.spec/coercion
                            :summary    "Get NLG result"
                            :middleware [muuntaja/format-request-middleware
                                         coercion/coerce-request-middleware]
                            :handler    service/get-result}
                  :delete  {:parameters {:path {:id string?}}
                            :responses  {200 {:body     ::service/generate-response
                                              :examples (get-in response-examples [:nlg :get])}}
                            :coercion   reitit.coercion.spec/coercion
                            :summary    "Delete NLG result"
                            :middleware [muuntaja/format-request-middleware
                                         coercion/coerce-request-middleware]
                            :handler    service/delete-result}
                  :options {:handler cors-handler
                            :no-doc  true}}]
     ["/accelerated-text-data-files/" {:parameters {:multipart {:file multipart/bytes-part}}
                                       :post       (fn [request]
                                                     (let [{params :params} (multipart-handler request)
                                                           id (data-files/store! (get params "file"))]
                                                       {:status 200
                                                        :body   {:message "Succesfully uploaded file" :id id}}))
                                       :coercion   reitit.coercion.spec/coercion
                                       :summary    "Upload a file"
                                       :responses  {200 {:body     {:message string?
                                                                    :id      string?}
                                                         :examples (get-in response-examples [:accelerated-text-data-files :post])}}}]
     ["/swagger.json" {:get {:no-doc  true
                             :swagger {:info {:title "nlg-api"}}
                             :handler (swagger/create-swagger-handler)}}]
     ["/health" {:get      {:summary   "Check API health"
                            :handler   health
                            :responses {200 {:body     {:health string?}
                                             :examples (get-in response-examples [:health :get])}}}
                 :coercion reitit.coercion.spec/coercion}]
     ["/status" {:get      {:summary   "Check service status"
                            :handler   status
                            :responses {200 {:body     {:color string? :services coll?}
                                             :examples (get-in response-examples [:status :get])}}}
                 :coercion reitit.coercion.spec/coercion}]]
    {:data      {:muuntaja   m/instance
                 :middleware [swagger/swagger-feature
                              muuntaja/format-negotiate-middleware
                              parameters/parameters-middleware
                              wrap-response
                              muuntaja/format-response-middleware
                              errors/exception-middleware]}
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
    (server/run-server
      #'app {:port     port
             :ip       host
             :max-body Integer/MAX_VALUE})))

(defstate http-server
  :start (start-http-server conf)
  :stop (http-server :timeout 100))

(defn -main [& _]
  (mount/start))
