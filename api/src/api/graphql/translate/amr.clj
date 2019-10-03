(ns api.graphql.translate.amr
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(defn role->schema
  [role]
  {:fieldType  [:STRING :LIST]
   :id         (string/lower-case (:type role))
   :fieldLabel (:type role)})

(defn frame->examples
  [frame]
  (:examples frame))

(defn examples->helpText
  [examples]
  (string/join "\n\n" examples))

(defn verbclass->schema
  [{:keys [id dictionary-item-id thematic-roles frames label] :as verbclass}]
  (log/debugf "Verbclass: %s" verbclass)
  {:id             id
   :dictionaryItem (or dictionary-item-id id)
   :roles          (map role->schema thematic-roles)
   :helpText       (examples->helpText (flatten (map frame->examples frames)))
   :label          (or label id)})
