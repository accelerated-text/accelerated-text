(ns api.nlg.ref-expr
  (:require [api.utils :as utils]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as http]))

(defn enable-ref-expr? []
  (Boolean/valueOf (System/getenv "ENABLE_REF_EXPR")))

(defn ref-expr-endpoint []
  (or (System/getenv "REF_EXPR_ENDPOINT") "http://localhost:8003"))

(defn ref-expr-request [content]
  (log/infof "Applying ref expressions via: %s" (ref-expr-endpoint))
  (let [{{:keys [result error message]} :body request-error :error}
        (-> @(http/request {:url     (ref-expr-endpoint)
                            :method  :post
                            :headers {"Content-Type" "application/json"}
                            :body    (json/write-value-as-string content)})
            (update :body #(json/read-value % utils/read-mapper)))]
    (cond
      (true? error) (log/errorf "Failed to apply ref rexpression on text: %s" message)
      (some? request-error) (log/errorf "Ref Expression request failed: %s" (.getMessage request-error))
      :else result)))

(defn apply-ref-expressions [lang text]
  (-> {:lang lang :text text}
      (ref-expr-request)
      :result))
