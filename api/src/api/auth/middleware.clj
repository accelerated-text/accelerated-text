(ns api.auth.middleware
    (:require [clojure.tools.logging :as log]
              [api.auth.service :as service]
              [clojure.walk :as walk]
              [clojure.string :as str]))

(defn normalize-headers [headers]
  (->> (map (fn [[k v]] [(str/lower-case k) v]) headers)
       (into {})
       (walk/keywordize-keys)))

(defn append-auth-info [{:keys [headers] :as req}]
  (let [token (:auth-token (normalize-headers headers))]
    (log/infof "Look, I'm doing something!. Token: %s" token))
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