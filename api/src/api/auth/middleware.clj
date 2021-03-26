(ns api.auth.middleware
    (:require [clojure.tools.logging :as log]
              [api.auth.service :as service]))

(defn append-auth-info [{:keys [headers] :as req}]
  (log/infof "Look, I'm doing something!. This is headers: %s" headers)
  req)

(defn wrapper
  [handler]
  (fn [req]
    (if (service/auth-enabled?)
      (handler (append-auth-info req))
      (handler req))
    (handler req)))


(def auth-middleware
  {:name ::auth
   :wrap wrapper})