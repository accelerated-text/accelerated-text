(ns data-access.utils
  (:require [clojure.string :as str])
  (:import (java.util UUID)
           (java.time Instant)))

(defn gen-uuid []
  (str (java.util.UUID/randomUUID)))

(defn ts-now []
  (int (.getEpochSecond (Instant/now))))
