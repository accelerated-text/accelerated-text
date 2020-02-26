(ns api.graphql.translate.concept
  (:require [clojure.string :as str]
            [data.utils :as utils]))

(defn- role->schema [{:keys [type label]}]
  {:id         (utils/gen-rand-str 16)
   :fieldType  (cond-> ["List" "Str"] (some? type) (conj type))
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
