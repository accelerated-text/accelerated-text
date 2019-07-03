(ns translate.amr
  (:require [clojure.tools.logging :as log]))

(defn member->schema [member] member)

(defn role->schema
  [role]
  {:type (:type role)})

(defn verbclass->schema
  [verbclass]
  {:id (:id verbclass)
   :members (map member->schema (:members verbclass))
   :roles (map role->schema (:thematic-roles verbclass))})
