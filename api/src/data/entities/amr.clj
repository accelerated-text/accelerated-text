(ns data.entities.amr
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [data.entities.document-plan :as dp]
            [data.utils :as utils]))

(defn get-relation-names [{relations ::sg/relations}]
  (reduce (fn [m {concept-id :to {name :name} :attributes}]
            (cond-> m
                    (some? name) (assoc concept-id name)))
          {}
          relations))

(defn document-plan->amr [{:keys [id name documentPlan] :as entity}]
  (let [{concepts ::sg/concepts :as semantic-graph} (document-plan->semantic-graph
                                                      documentPlan
                                                      {:var-names (dp/get-variable-names entity)})]
    {:id             id
     :label          name
     :kind           "Str"
     :semantic-graph semantic-graph
     :roles          (let [relation-names (get-relation-names semantic-graph)]
                       (loop [[reference & rs] (filter #(= :reference (:type %)) concepts)
                              index 0 vars #{} roles []]
                         (if-not (some? reference)
                           roles
                           (let [{id :id {name :name} :attributes} reference]
                             (recur
                               rs
                               (inc index)
                               (conj vars name)
                               (cond-> roles
                                       (nil? (get vars name)) (conj {:id    (format "ARG%d" index)
                                                                     :label name
                                                                     :type  (-> (get relation-names id)
                                                                                (str/split #"/")
                                                                                (last))})))))))}))

(defn read-amr [id content]
  (let [{:keys [roles kind frames]} (yaml/parse-string content)]
    {:id     id
     :roles  (map (fn [role] {:type role}) roles)
     :kind   (or kind "Str")
     :frames (map (fn [{:keys [syntax example]}]
                    {:examples [example]
                     :syntax   (for [instance syntax]
                                 (reduce-kv (fn [m k v]
                                              (assoc m k (cond->> v
                                                                  (contains? #{:pos :type} k) (keyword)
                                                                  (= :params k) (map #(select-keys % [:role :type])))))
                                            {}
                                            (into {} instance)))})
                  frames)}))

(defn grammar-package []
  (io/file (or (System/getenv "AMR_GRAMMAR") "grammar/concept-net.yaml")))

(defn list-amr-files
  ([] (list-amr-files (grammar-package)))
  ([package]
   (let [parent (.getParent (io/file package))]
     (->> package
          (slurp)
          (yaml/parse-string)
          (:includes)
          (map (partial io/file parent))))))

(defn get-amr [id]
  (or (some-> id (dp/get-document-plan) (document-plan->amr))
      (some #(when (= id (:id %)) %) (map #(read-amr (utils/get-name %) (slurp %)) (list-amr-files "grammar/all.yaml")))))

(defn list-amrs []
  (concat
    (map document-plan->amr (dp/list-document-plans "AMR"))
    (map #(read-amr (utils/get-name %) (slurp %)) (list-amr-files))))

(defn list-rgls []
  (map document-plan->amr (dp/list-document-plans "RGL")))
