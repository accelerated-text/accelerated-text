(ns api.resource
  (:require [api.utils :as utils]
            [data.utils :refer [ts-now]]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [jsonista.core :as json])
  (:import (java.io BufferedWriter)
           (java.net URLDecoder)))

(def response-examples
  {:nlg                         {:post {:application/json {:resultId   (utils/gen-uuid),
                                                           :offset     0,
                                                           :totalCount 1,
                                                           :ready      true,
                                                           :updatedAt  (ts-now),
                                                           :variants   ["Text value."]}}
                                 :get  {:application/json {:resultId   (utils/gen-uuid),
                                                           :offset     0,
                                                           :totalCount 1,
                                                           :ready      true,
                                                           :updatedAt  (ts-now),
                                                           :variants   ["Text value."]}}}
   :nlg-bulk                    {:post {:application/json {:resultIds (take 10 (repeatedly utils/gen-uuid))}}}
   :accelerated-text-data-files {:post   {:application/json {:message "Succesfully uploaded file"
                                                             :id      (utils/gen-uuid)}}
                                 :delete {:application/json {:message "Succesfully deleted file"
                                                             :id      (utils/gen-uuid)}}}
   :health                      {:get {:application/json {:health "Ok"}}}
   :status                      {:get {:application/json {:color    "green"
                                                          :services {"service" true}}}}})


(defn- dummy [& _]
  {:status 200
   :body   {:text "I do nothing."}})

(defn decode-vals [m]
  (when m
    (reduce-kv (fn [m k v]
                 (assoc m k (URLDecoder/decode v)))
               {}
               m)))

(defn decode-input [is decode-body?]
  (-> is
      (utils/read-json-is)
      (update :pathParameters decode-vals)
      (update :queryStringParameters decode-vals)
      (cond-> (true? decode-body?)
              (update :body #(json/read-value % utils/read-mapper)))))

(defn- generate-response [status-code body]
  (json/write-value-as-string
    {:statusCode      status-code
     :isBase64Encoded false
     :body            (if (some? body) (json/write-value-as-string body) "")
     :headers         (cond-> {"Access-Control-Allow-Origin"  "*"
                               "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
                               "Access-Control-Allow-Headers" "*, Content-Type"}
                              (some? body) (assoc "Content-Type" "application/json"))}))

(defn build-resource [{:keys [get-handler post-handler delete-handler put-handler]} decode-body?]
  (fn [_ is os _]
    (let [{path-params  :pathParameters
           query-params :queryStringParameters
           method       :httpMethod
           body         :body :as input} (decode-input is decode-body?)
          {:keys [status body]} (case (keyword method)
                                  :GET (if get-handler (get-handler query-params path-params) (dummy))
                                  :DELETE (if delete-handler (delete-handler path-params) (dummy))
                                  :POST (if post-handler (post-handler body) (dummy))
                                  :PUT (if put-handler (put-handler path-params body) (dummy))
                                  :OPTIONS {:status 200})]
      (log/debugf "Method: %s" method)
      (log/debugf "Path Params: %s" path-params)
      (log/debugf "Query Params: %s" query-params)
      (log/debugf "Received '%s' and produced output '%s'" input body)
      (with-open [^BufferedWriter w (io/writer os)]
        (.write w ^String (generate-response status body))))))
