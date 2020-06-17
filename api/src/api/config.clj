(ns api.config
  (:require [clojure.string :as str]
            [mount.core :refer [defstate]]))

(defn- read-conf-line [l]
  (map (comp str/capitalize str/trim) (str/split l #",")))

(defn load-config []
  {:port                (Integer/valueOf ^String (or (System/getenv "ACC_TEXT_API_PORT") "3001"))
   :host                (or (System/getenv "ACC_TEXT_API_HOST") "0.0.0.0")
   :db-implementation   (when-let [db-implementation (or (System/getenv "DB_IMPLEMENTATION") "datomic")]
                          (keyword db-implementation))
   :db-uri              (System/getenv "DB_URI")
   :available-languages (set (read-conf-line (or (System/getenv "AVAILABLE_LANGUAGES") "Eng")))
   :enabled-languages   (set (read-conf-line (or (System/getenv "ENABLED_LANGUAGES") "Eng")))
   :dictionary-path     (or (System/getenv "DICT_PATH") "resources/dictionary")})

(defstate conf :start (load-config))
