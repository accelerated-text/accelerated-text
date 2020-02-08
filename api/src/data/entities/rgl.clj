(ns data.entities.rgl
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [data.utils :as utils]
            [clojure.string :as str]))

(defn read-rgl [f]
  (let [instance (utils/read-edn f)]
    (for [{:keys [function type example]} (:functions instance)]
      (let [roles (subvec type 0 (dec (count type)))
            ret (last type)]
        {:id     (str function "/" (str/join "->" type))
         :kind   (last type)
         :roles  (map (fn [role] {:type role}) roles)
         :label  function
         :name   (str/join " -> " type)
         :frames [{:examples [example]
                   :syntax   [{:type   :oper
                               :value  function
                               :ret    ret
                               :params (map (fn [role] {:type role}) roles)}]}]}))))

(defn list-package [package]
  (let [abs-path (.getParent (io/file package))]
    (->> package
         (utils/read-yaml)
         (:includes)
         (map (fn [p] (io/file (string/join "/" [abs-path p])))))))

(defn list-rgl-files []
  (list-package (or (System/getenv "RGL_GRAMMAR_PACKAGE") "../grammar/rgl.yaml")))

(defn load-all []
  (mapcat read-rgl (list-rgl-files)))

(defn load-single [id]
  (some #(when (= id (:id %)) %) (load-all)))
