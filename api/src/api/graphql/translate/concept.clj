(ns api.graphql.translate.concept
  (:require [acc-text.nlg.gf.paths :refer [possible-paths]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.string :as str]
            [data.utils :as utils]))

(defn- role->schema [{:keys [name category]}]
  {:id         (utils/gen-rand-str 16)
   :fieldLabel (or name category "Str")
   :fieldType  (cond-> ["List" "Str"]
                       (some? category) (-> (concat (get possible-paths category))
                                            (distinct)
                                            (vec)
                                            (conj category)))})

(defn operation->schema [{:keys [id name category args example]}]
  {:id       id
   :kind     category
   :roles    (map #(role->schema {:name % :category (str/replace % #"[()]" "")}) args)
   :helpText (str example)
   :label    name
   :name     (str/join " -> " (conj args category))})

(defn select-roles [roles]
  (let [role-position (zipmap (distinct (map :name roles)) (range))]
    (->> roles
         (group-by :name)
         (vals)
         (map (comp #(or % "Str") first reverse #(sort-by :category %)))
         (sort-by (comp role-position :name)))))

(defn amr->schema [{::sg/keys [id category description name] :as entity}]
  (let [roles (select-roles (sg-utils/find-roles entity))]
    {:id       id
     :kind     category
     :roles    (map role->schema roles)
     :helpText (str description)
     :label    name
     :name     (str/join " -> " (-> (mapv #(get % :category "Str") roles) (conj category)))}))
