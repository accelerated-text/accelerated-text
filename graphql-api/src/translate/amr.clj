(ns translate.amr
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]))

(defn member->schema [member] member)

(defn role->schema
  [role]
  {:type "TEXT"
   :id (string/lower-case (:type role))
   :label (:type role)})

(defn verbclass->schema
  [verbclass]
  {:id (:id verbclass)
   :members (map member->schema (:members verbclass))
   :roles (map role->schema (:thematic-roles verbclass))})
