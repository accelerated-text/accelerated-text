(ns data.entities.rgl
  (:require [api.config :refer [conf]]
            [clojure.string :as str]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate rgl-db :start (db/db-access :rgl conf))

(defn list-rgls []
  (db/list! rgl-db 100))

(defn get-rgl [id]
  (when-not (str/blank? id)
    (db/read! rgl-db id)))

(defn delete-rgl [id]
  (db/delete! rgl-db id))

(defn write-rgl [{id :id :as entity}]
  (when (get-rgl id)
    (delete-rgl id))
  (db/write! rgl-db id entity))

(defn rgl-syntax-path []
  (or (System/getenv "GRAMMAR_SYNTAX") "resources/syntax"))

(defn rgl-paradigms-path []
  (or (System/getenv "GRAMMAR_PARADIGMS") "resources/paradigms"))

(defn read-rgl [f]
  (let [{:keys [module functions]} (utils/read-edn f)]
    (for [{:keys [function type example]} functions]
      (let [roles (subvec type 0 (dec (count type)))]
        {:id     (cond->> (str function "/" (str/join "->" type)) (some? module) (str module "."))
         :kind   (last type)
         :roles  (map (fn [role] {:type (str/replace role #"[()]" "") :label role}) roles)
         :label  function
         :name   (str/join " -> " type)
         :module module
         :frames [{:examples [example]
                   :syntax   [{:type   :oper
                               :value  (cond->> function (some? module) (str module "."))
                               :ret    (last type)
                               :params (map (fn [role index]
                                              {:id   (format "ARG%d" index)
                                               :type role})
                                            roles (range))}]}]}))))

(defn grammar-type? [{:keys [kind]}]
  (contains? #{"Tense" "Punct" "Pol" "Num" "ImpForm" "Ant"} kind))

(defn read-library
  ([]
   (read-library (rgl-syntax-path)))
  ([path]
   (->> (utils/list-files path)
        (mapcat read-rgl)
        (filter #(or (seq (:roles %)) (grammar-type? %)))
        (sort-by #(vector (:kind %) (:label %))))))

(defn read-structural-words
  ([]
   (read-structural-words (rgl-syntax-path)))
  ([path]
   (->> (utils/list-files path)
        (mapcat read-rgl)
        (filter #(and (empty? (:roles %)) (not (grammar-type? %))))
        (sort-by #(vector (:kind %) (:label %))))))

(defn read-grammar
  ([]
   (read-grammar (rgl-syntax-path)))
  ([path]
   (->> (utils/list-files path)
        (mapcat read-rgl)
        (filter #(and (empty? (:roles %)) (grammar-type? %)))
        (sort-by #(vector (:kind %) (:label %))))))

(defn read-paradigms
  ([]
   (read-paradigms (rgl-paradigms-path)))
  ([path]
   (mapcat read-library (utils/list-directories path))))

(defn initialize
  ([] (initialize (concat (read-library) (read-structural-words) (read-paradigms))))
  ([entities]
   (doseq [entity entities]
     (write-rgl entity))))
