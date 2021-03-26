(ns api.auth
    (:require [clojure.tools.logging :as log]))

(defn auth-enabled?
  [config]
  (not (nil? (:auth-url config))))