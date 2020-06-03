(ns api.graphql.translate.concept
  (:require [acc-text.nlg.gf.paths :refer [possible-paths]]
            [clojure.string :as str]
            [data.utils :as utils]))

(defn- role->schema [{:keys [type label]}]
  {:id         (utils/gen-rand-str 16)
   :fieldLabel (or label type "")
   :fieldType  (cond-> ["List" "Str"]
                       (some? type) (-> (concat (get possible-paths type))
                                        (distinct)
                                        (vec)
                                        (conj type)))})

(defn- frames->help-text [frames]
  (->> frames
       (map :examples)
       (flatten)
       (str/join "\n\n")))

(defn- construct-name [{:keys [kind roles]}]
  (str/join " -> " (-> (mapv #(or (:type %) (:label %)) roles) (conj kind))))

(defn operation->schema [{:keys [id name category args example]}]
  {:id       id
   :kind     category
   :roles    (map #(role->schema {:type (str/replace % #"[()]" "") :label %}) args)
   :helpText (str example)
   :label    name
   :name     (str/join " -> " (conj args category))})

(defn amr->schema [{:keys [id kind roles frames label name] :as entity}]
  {:id       id
   :kind     (or kind "Str")
   :roles    (map role->schema roles)
   :helpText (frames->help-text frames)
   :label    (or label id)
   :name     (or name (construct-name entity))})
