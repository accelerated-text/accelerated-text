(ns api.error
  (:require [reitit.ring.middleware.exception :as exception]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :as st]))

(derive ::error ::exception)
(derive ::failure ::exception)

(defn exception-handler [type exception request]
  {:status 500
   :body   {:type      type
            :exception (.getClass exception)
            :data      (ex-data exception)
            :uri       (:uri request)}})

(def exception-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    {;; ex-data with :type ::error
     ::error             (partial exception-handler "error")

     ;; ex-data with ::exception or ::failure
     ::exception         (partial exception-handler "exception")

     ;; override the default handler
     ::exception/default (partial exception-handler "default")

     ;; print stack-traces for all exceptions
     ::exception/wrap    (fn [handler e request]
                           (log/errorf "Uri: %s Stacktrace: \n%s" (:uri request) (with-out-str (st/print-stack-trace e)))
                           (handler e request))})))
