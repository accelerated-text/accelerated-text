(ns acc-text.nlg.gf.operations
  (:require [acc-text.nlg.gf.utils :as utils]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:import (java.io File)))

(def rgl-syntax (io/resource "rgl/syntax"))

(def rgl-paradigms (io/resource "rgl/paradigms"))

(def operations
  (->> [rgl-syntax rgl-paradigms]
       (mapcat (comp file-seq io/file))
       (remove #(.isDirectory ^File %))
       (mapcat (fn [f]
                 (let [{:keys [module functions]} (utils/read-edn f)]
                   (for [{:keys [function type example]} functions]
                     {:id       (cond->> (str function "/" (str/join "->" type))
                                         (some? module) (str module "."))
                      :type     :operation
                      :name     function
                      :category (last type)
                      :args     (vec (butlast type))
                      :module   module
                      :example  example}))))
       (sort-by #(vector (:category %) (:name %)))))

(def operation-map (zipmap (map :id operations) operations))

(def structural-words (filter (fn [{:keys [category args]}]
                                (and
                                  (empty? args)
                                  (not (contains? #{"Tense" "Punct" "Pol"
                                                    "Num" "ImpForm" "Ant"}
                                                  category))))
                              operations))

(def grammar (filter (fn [{:keys [category args]}]
                       (and
                         (empty? args)
                         (contains? #{"Tense" "Punct" "Pol"
                                      "Num" "ImpForm" "Ant"}
                                    category)))
                     operations))

(def syntax (filter (fn [{:keys [module]}]
                      (= "Syntax" module))
                    operations))

(def paradigms (filter (fn [{:keys [module]}]
                         (not= "Syntax" module))
                       operations))
