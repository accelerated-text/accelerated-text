(ns api.auth.service
    (:require [clojure.tools.logging :as log]))

(defn auth-enabled?
  [config]
  (not (nil? (:auth-url config))))