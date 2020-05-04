(ns api.config
  (:require [mount.core :refer [defstate]]
            [clojure.string :as string]))

(defn load-config []
  {:port              (Integer/valueOf ^String (or (System/getenv "ACC_TEXT_API_PORT") "3001"))
   :host              (or (System/getenv "ACC_TEXT_API_HOST") "0.0.0.0")
   :db-implementation (when-let [db-implementation (or (System/getenv "DB_IMPLEMENTATION") "datomic")]
                        (keyword db-implementation))
   :db-uri            (System/getenv "DB_URI")
   :enabled-languages (map #(-> % string/trim string/capitalize)
                           (string/split (or (System/getenv "ENABLED_LANGUAGES") "english") #","))})

(defstate conf :start (load-config))
