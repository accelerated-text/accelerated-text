(ns utils.config
  (:require [mount.core :refer [defstate]]))

(defn load-config [] {:api-url (or (System/getenv "API_URL") "http://0.0.0.0:3001")})

(defstate config :start (load-config))
