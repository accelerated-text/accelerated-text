(ns api.auth.service
    (:require [clojure.tools.logging :as log]
              [api.config :refer [conf]]))

(defn auth-enabled?
  []
  (not (nil? (:auth-url conf))))