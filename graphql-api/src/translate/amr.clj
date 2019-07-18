(ns translate.amr
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]))

(defn role->schema
  [role]
  {:type "TEXT"
   :id (string/lower-case (:type role))
   :label (:type role)})

(defn frame->examples
  [frame]
  (:examples frame))

(defn verbclass->schema
  [verbclass]
  {:id (:id verbclass)
   :dictionaryItem (:dictionaryItem verbclass)
   :roles (map role->schema (:thematic-roles verbclass))
   :examples (flatten (map frame->examples (:frames verbclass)))})
