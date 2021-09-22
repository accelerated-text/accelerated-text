(ns api.graphql.translate.concept
  (:require [acc-text.nlg.gf.paths :as paths]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.string :as str]
            [data.utils :as utils]))

(defn- role->schema [{:keys [name category]}]
  {:id         (utils/gen-rand-str 16)
   :fieldLabel (or name category "Str")
   :fieldType  (cond-> ["List" "Str"]
                 (some? category) (-> (concat (get paths/possible-paths category))
                                      (distinct)
                                      (vec)
                                      (conj category)))})

(defn normalize-category [cat]
  (when-not (str/blank? cat)
    (str/replace cat #"[()]" "")))

(defn operation->schema [{:keys [id label category args example]}]
  {:id       id
   :kind     category
   :roles    (map #(role->schema {:name % :category (normalize-category %)}) args)
   :helpText (str example)
   :label    label
   :name     (str/join " -> " (conj args category))})

(defn find-categories [roles]
  (reduce-kv (fn [m k v]
               (assoc m k (paths/get-intersection (map (comp normalize-category :category) v))))
             {}
             (group-by :name roles)))

(defn select-roles [roles]
  (let [role-category (find-categories roles)
        role-position (zipmap (distinct (map :name roles)) (range))]
    (->> roles
         (group-by :name)
         (vals)
         (map (comp #(assoc % :category (or (get role-category (:name %)) "Str")) first))
         (sort-by (comp role-position :name)))))

(defn amr->schema [{::sg/keys [id category description name] :as entity}]
  (let [roles (select-roles (sg-utils/find-roles entity))]
    {:id       id
     :kind     category
     :roles    (map role->schema roles)
     :helpText (str description)
     :label    name
     :name     (str/join " -> " (-> (mapv #(get % :category "Str") roles) (conj category)))}))
