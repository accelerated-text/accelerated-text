(ns lambda.core
  (:gen-class)
  (:require [graphql.core :as graphql]
            [jsonista.core :as json]
            [org.httpkit.client :as http])
  (:import (org.apache.commons.logging.impl LogFactoryImpl)))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn get-lambda-invocation-request [runtime-api]
  @(http/request
     {:method  :get
      :url     (str "http://" runtime-api "/2018-06-01/runtime/invocation/next")
      :timeout 900000}))

(defn send-response [runtime-api lambda-runtime-aws-request-id response-body]
  @(http/request
     {:method  :post
      :url     (str "http://" runtime-api "/2018-06-01/runtime/invocation/" lambda-runtime-aws-request-id "/response")
      :body    response-body
      :headers {"Content-Type" "application/json"}}))

(defn send-error [runtime-api lambda-runtime-aws-request-id error-body]
  @(http/request
     {:method  :post
      :url     (str "http://" runtime-api "/2018-06-01/runtime/invocation/" lambda-runtime-aws-request-id "/error")
      :body    error-body
      :headers {"Content-Type" "application/json"}}))

(defn request->response [request]
  (let [decoded-request (json/read-value request read-mapper)]
    (json/write-value-as-string
      {:headers         {"Access-Control-Allow-Origin"  "*"
                         "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}
       :statusCode      200
       :isBase64Encoded false
       :body            (json/write-value-as-string
                          (graphql/nlg
                            (json/read-value (:body decoded-request) read-mapper)))})))

(defn -main [& _]
  (let [runtime-api (System/getenv "AWS_LAMBDA_RUNTIME_API")
        _ (LogFactoryImpl.)]
    (while true
      (let [{request-body                            :body
             {:keys [lambda-runtime-aws-request-id]} :headers
             error                                   :error} (get-lambda-invocation-request runtime-api)]
        (when error
          (send-error runtime-api lambda-runtime-aws-request-id (str error))
          (throw (Exception. (str error))))
        (try
          (let [resp (send-response runtime-api
                                    lambda-runtime-aws-request-id
                                    (request->response request-body))]
            resp)
          (catch Exception e
            (.printStackTrace e)
            (send-error runtime-api lambda-runtime-aws-request-id (str (.getMessage e)))))))))
