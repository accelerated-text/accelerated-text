(ns api.config
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [mount.core :refer [defstate]]))

(defn- read-conf-line [l]
  (map (comp str/capitalize str/trim) (str/split l #",")))

(defn load-config []
  {:port                 (Integer/valueOf ^String (or (System/getenv "ACC_TEXT_API_PORT") "3001"))
   :host                 (or (System/getenv "ACC_TEXT_API_HOST") "0.0.0.0")
   :db-implementation    (when-let [db-implementation (or (System/getenv "DB_IMPLEMENTATION") "datomic")]
                           (keyword db-implementation))
   :db-uri               (System/getenv "DB_URI")
   :display-error        (Boolean/valueOf ^String (or (System/getenv "DISPLAY_ERROR") "TRUE"))
   :default-language     (or (System/getenv "DEFAULT_LANGUAGE") "Eng")
   :enabled-languages    (set (read-conf-line (or (System/getenv "ENABLED_LANGUAGES") "Eng")))
   :enabled-readers      (set (read-conf-line (or (System/getenv "ENABLED_READERS") "")))
   :config-path          (or (System/getenv "CONFIG_PATH") (io/resource "config"))
   :dictionary-path      (or (System/getenv "DICT_PATH") (io/resource "dictionary"))
   :document-plan-path   (or (System/getenv "DOCUMENT_PLANS") (io/resource "document-plans"))
   :relevant-items-limit (or (System/getenv "RELEVANT_ITEMS_MATRIX_LIMIT") 100)
   :auth-url             (System/getenv "AUTH_URL")
   :remove-duplicates    (Boolean/valueOf ^String (or (System/getenv "REMOVE_DUPLICATES") "TRUE"))
   :enable-cache         (Boolean/valueOf ^String (or (System/getenv "ENABLE_CACHE") "FALSE"))
   :relevant-items-limit (or (System/getenv "RELEVANT_ITEMS_MATRIX_LIMIT") 100)})

(defstate conf :start (load-config))
