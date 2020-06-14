(ns api.config
  (:require [mount.core :refer [defstate]]
            [clojure.string :as str]))

(defn- read-conf-line [l]
  (map (comp str/capitalize str/trim) (str/split l #",")))

(defn load-config []
  {:port                (Integer/valueOf ^String (or (System/getenv "ACC_TEXT_API_PORT") "3001"))
   :host                (or (System/getenv "ACC_TEXT_API_HOST") "0.0.0.0")
   :db-implementation   (when-let [db-implementation (or (System/getenv "DB_IMPLEMENTATION") "datomic")]
                          (keyword db-implementation))
   :db-uri              (System/getenv "DB_URI")
   :available-languages (set (read-conf-line (or (System/getenv "AVAILABLE_LANGUAGES") "English")))
   :enabled-languages   (set (read-conf-line (or (System/getenv "ENABLED_LANGUAGES") "English")))})

(defstate conf :start (load-config))
