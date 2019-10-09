(ns api.graphql.translate.amr
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(defn- role->schema [role]
  {:fieldType  [:STRING :LIST]
   :id         (string/lower-case (:type role))
   :fieldLabel (:type role)})

(defn- frames->helpText [frames]
  (->> frames
       (map :examples)
       (flatten)
       (string/join "\n\n")))

(defn verbclass->schema [{:keys [id dictionary-item-id thematic-roles frames label] :as verbclass}]
  (log/debugf "Verbclass: %s" verbclass)
  {:id             id
   :dictionaryItem (or dictionary-item-id id)
   :roles          (map role->schema thematic-roles)
   :helpText       (frames->helpText frames)
   :label          (or label id)})
