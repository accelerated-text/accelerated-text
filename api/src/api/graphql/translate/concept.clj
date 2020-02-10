(ns api.graphql.translate.concept
  (:require [clojure.string :as string]))

(defn- role->schema [{:keys [id type label]}]
  {:id         (or id type)
   :fieldType  (cond-> ["Str" "List"] (some? type) (conj type))
   :fieldLabel (or label type "")})

(defn- frames->help-text [frames]
  (->> frames
       (map :examples)
       (flatten)
       (string/join "\n\n")))

(defn amr->schema [{:keys [id kind roles frames label name]}]
  {:id       id
   :kind     kind
   :roles    (map role->schema roles)
   :helpText (frames->help-text frames)
   :label    (or label id)
   :name     (or name id)})
