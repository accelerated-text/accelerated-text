(ns data-access.utils
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [clojure.tools.logging :as log])
  (:import (java.util UUID)))

(defn gen-uuid [] (.toString (java.util.UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))
