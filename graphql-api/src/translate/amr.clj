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

(defn examples->helpText
  [examples]
  (string/join "\n" examples))

(defn verbclass->schema
  [verbclass]
  (log/debugf "Verbclass: %s" verbclass)
  {:id (:id verbclass)
   :dictionaryItem (:dictionary-item-id verbclass)
   :roles (map role->schema (:thematic-roles verbclass))
   :helpText (examples->helpText (flatten (map frame->examples (:frames verbclass))))})
