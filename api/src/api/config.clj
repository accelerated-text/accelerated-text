(ns api.config
  (:require [mount.core :refer [defstate]]))

(defn load-config []
  {:port              (Integer/valueOf ^String (or (System/getenv "ACC_TEXT_API_PORT") "3001"))
   :host              (or (System/getenv "ACC_TEXT_API_HOST") "0.0.0.0")
   :db-implementation (when-let [db-implementation (System/getenv "DB_IMPLEMENTATION")]
                        (keyword db-implementation))})

(defstate conf :start (load-config))
