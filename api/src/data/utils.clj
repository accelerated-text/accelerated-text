(ns data.utils
  (:import (java.util UUID)
           (java.time Instant)))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (int (.getEpochSecond (Instant/now))))
