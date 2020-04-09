(ns utils.config
  (:require [mount.core :refer [defstate]]))

(defn load-config [] {:graphql-url (or (System/getenv "GRAPHQL_URL") "http://localhost:3001/_graphql")})

(defstate config :start (load-config))
