(ns api.graphql.translate.concept
  (:require [clojure.string :as string]))

(defn- role->schema [{type :type}]
  {:fieldType  ["Str" "List" type]
   :id         type
   :fieldLabel type})

(defn- frames->help-text [frames]
  (->> frames
       (map :examples)
       (flatten)
       (string/join "\n\n")))

(defn amr->schema [{:keys [id kind thematic-roles frames label]}]
  {:id       id
   :kind     kind
   :roles    (map role->schema thematic-roles)
   :helpText (frames->help-text frames)
   :label    (or label id)})
