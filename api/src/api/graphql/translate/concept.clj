(ns api.graphql.translate.concept
  (:require [clojure.string :as string]))

(defn- role->schema [{type :type}]
  {:fieldType  [:STRING :LIST]
   :id         (string/lower-case type)
   :fieldLabel type})

(defn- frames->help-text [frames]
  (->> frames
       (map :examples)
       (flatten)
       (string/join "\n\n")))

(defn amr->schema [{:keys [id dictionary-item-id thematic-roles frames label]}]
  {:id             id
   :dictionaryItem (or dictionary-item-id id)
   :roles          (map role->schema thematic-roles)
   :helpText       (frames->help-text frames)
   :label          (or label id)})
