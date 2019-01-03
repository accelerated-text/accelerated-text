(ns lt.tokenmill.nlg.generator.segment
  (:require [lingo.core :refer :all]
            [lingo.features :refer :all]))

;; The Nike Air Max 95 Premium provides exceptional support and comfort with a sleek update on a classic design. Its premium lacing results in a snug fit for everyday wear.

(def generator (make-gen))

(defn dummy-template
  [product-name rel features]
  (let [feature-clause (map (fn [f] {:> :complement :+ f}) features)
        root [{:> :subject :+ ["the" product-name]}{:> :verb :+ rel}]]
    {:> :clause
     :+ (concat root feature-clause)}))
 
(defn dummy-product
  [product-name rel & features]
  ((:! generator) (dummy-template product-name rel features)))
