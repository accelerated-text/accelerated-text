(ns lt.tokenmill.nlg.generator.segment
  (:require [lt.tokenmill.nlg.generator.schemas :as schemas]))
 
(defn select-values [map ks]
         (reduce #(conj %1 (map %2)) [] ks))


(defn generate-text
  ;; Lets imagine magical way to select proper template by given arguments
  ;; Expect `args` to be a hashmap
  [args]
  (let [templates (schemas/matching-templates args)]
    (if (not (empty? templates))
      (let [selected (first templates)]
        (apply (selected :fn) (select-values args [:product-name :relation :adverb :features])))
      {:error "No Matching templates for this argument group"})))
