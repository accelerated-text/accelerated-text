(ns acc-text.nlg.gf.operations
  (:require [acc-text.nlg.gf.utils :as utils]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:import (java.io File)))

(def rgl-syntax (io/resource "rgl/syntax"))

(def rgl-paradigms (io/resource "rgl/paradigms"))

(def rgl-grammar (io/resource "rgl/grammar"))

(def operations
  (->> [rgl-syntax rgl-paradigms rgl-grammar]
       (mapcat (comp file-seq io/file))
       (remove #(.isDirectory ^File %))
       (mapcat (fn [f]
                 (let [{:keys [module functions]} (utils/read-edn f)]
                   (for [{:keys [function label type example]} functions]
                     {:id       (cond->> (str function "/" (str/join "->" type))
                                         (some? module) (str module "."))
                      :type     :operation
                      :name     function
                      :label    (or label function)
                      :category (last type)
                      :args     (vec (butlast type))
                      :module   module
                      :example  example}))))))

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
                         (re-matches #"^Paradigms.+" module))
                       operations))

(def extra (filter (fn [{:keys [module]}]
                     (and (not= "Syntax" module)
                          (not (re-matches #"^Paradigms.+" module))))
                   operations))
