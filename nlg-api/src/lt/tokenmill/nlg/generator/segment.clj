(ns lt.tokenmill.nlg.generator.segment
  (:require [lt.tokenmill.nlg.generator.simple-nlg :as nlg]))

;; The Nike Air Max 95 Premium provides exceptional support and comfort with a sleek update on a classic design. Its premium lacing results in a snug fit for everyday wear.
 
(defn dummy-product
  [product-name rel & features]
  (let [gen (nlg/generator)]
    (gen
     (fn
       [clause factory]
       (do
         (nlg/add-subj clause (nlg/create-noun factory "the" product-name))
         (nlg/add-verb clause rel)
         (nlg/add-obj clause (nlg/concat-multi factory features)))))))

