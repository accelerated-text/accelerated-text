(ns api.graphql.translate.concept
  (:require [clojure.string :as str]))

(defn- role->schema [{:keys [type label]}]
  {:id         (->> [label type] (filter some?) (str/join "/"))
   :fieldType  (cond-> ["Str" "List"] (some? type) (conj type))
   :fieldLabel (or label type "")})

(defn- frames->help-text [frames]
  (->> frames
       (map :examples)
       (flatten)
       (str/join "\n\n")))

(defn amr->schema [{:keys [id kind roles frames label name]}]
  {:id       id
   :kind     kind
   :roles    (map role->schema roles)
   :helpText (frames->help-text frames)
   :label    (or label id)
   :name     (or name id)})
