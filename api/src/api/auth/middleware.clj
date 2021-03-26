(ns api.auth.middleware
    (:require [clojure.tools.logging :as log]))

(defn wrapper
  [handler]
  (fn [{:keys [headers] :as req}]
    (log/infof "Look, I'm doing something!. This is headers: %s" headers)
    (handler req)))


(def auth-middleware
  {:name ::auth
   :wrap wrapper})