(ns acc-text.nlg.verbnet.cf
  (:require [acc-text.nlg.gf.cf-format :as cf]
            [clojure.string :as string]))

(defn frame->cf
  [themrole-idx initial {syntax :syntax}]
  (let [[head & body] (map
                       (fn
                         [{:keys [pos value]}]
                         (case pos
                           :NP (get themrole-idx value)
                           :LEX (format "\"%s\"" value)
                           :VERB "VB"
                           :PREP (format "\"%s\"" value)))
                       syntax)]
    (concat
     [(cf/gf-syntax-item "Pred" "S" (format "%s VP" head))
     (cf/gf-syntax-item "Compl" "VP" (string/join " " body))]
     initial)))

(defn vn->cf
  [{:keys [members frames thematic-roles]}]
  (let [themrole-idx (into {} (map-indexed (fn [idx {type :type}] [type (format "NP%d" idx)]) thematic-roles))
        initial (concat
                 (map (fn [{name :name}] (cf/gf-morph-item "Action" "VB" name)) members)
                 (map (fn [[k v]] (cf/gf-morph-item "Actor" v (cf/data-morphology-value k))) themrole-idx))]
    (-> (partial frame->cf themrole-idx initial)
        (map frames))))

