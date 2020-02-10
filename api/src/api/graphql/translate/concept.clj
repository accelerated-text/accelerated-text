(ns api.graphql.translate.concept
  (:require [clojure.string :as string]))

(defn- role->schema [{:keys [type label]}]
  {:fieldType  (cond-> ["Str" "List"]
                       (some? type) (conj type))
   :id         type
   :fieldLabel (or label type "UNK")})

(defn- frames->help-text [frames]
  (->> frames
       (map :examples)
       (flatten)
       (string/join "\n\n")))

(defn amr->schema [{:keys [id kind roles frames label name]}]
  {:id       id
   :kind     kind
   :roles    (->> roles
                  (map role->schema)
                  (group-by :id)
                  (vals)
                  (map first)
                  (remove (comp nil? :id)))
   :helpText (frames->help-text frames)
   :label    (or label id)
   :name     (or name id)})
