(ns api.graphql.translate.concept
  (:require [clojure.string :as string]))

(defn- role->schema [{type :type}]
  {:fieldType  [:STRING :LIST]
   :id         type
   :fieldLabel type})

(defn- frames->help-text [frames]
  (->> frames
       (map :examples)
       (flatten)
       (string/join "\n\n")))

(defn amr->schema [{:keys [id roles frames label]}]
  {:id       id
   :roles    (map role->schema roles)
   :helpText (frames->help-text frames)
   :label    (or label id)})
