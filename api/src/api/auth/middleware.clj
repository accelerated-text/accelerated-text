(ns api.auth.middleware
  (:require [api.auth.service :as service]
            [clojure.walk :as walk]
            [clojure.string :as str]
            [data.entities.user-group :as user-group]))

(defn normalize-headers [headers]
  (->> (map (fn [[k v]] [(str/lower-case k) v]) headers)
       (into {})
       (walk/keywordize-keys)))

(defn append-auth-info [{:keys [headers] :as req}]
  (let [token (:auth-token (normalize-headers headers))
        info  (service/request token)]
    (assoc req :auth-info info)))

(defn dummy-auth-info [req]
  (assoc req :auth-info {:group-id user-group/DUMMY-USER-GROUP-ID :username "default"}))

(defn wrapper
  [handler]
  (fn [req]
    (if (service/auth-enabled?)
      (handler (append-auth-info req))
      (handler (dummy-auth-info req)))))

(def auth-middleware
  {:name ::auth
   :wrap wrapper})
