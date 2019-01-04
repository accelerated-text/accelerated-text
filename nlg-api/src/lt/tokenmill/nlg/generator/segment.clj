(ns lt.tokenmill.nlg.generator.segment
  (:require [lt.tokenmill.nlg.generator.simple-nlg :as nlg]))
 
(defn product-template-1
  ;; The <product-name> <has|provides|etc.> <some-adverb> <features-list>
  ;; Example: The Nike Air Max 95 Premium provides exceptional support and comfort
  [product-name rel adverb & features]
  (let [gen (nlg/generator)]
    (gen
     (fn
       [clause factory]
       (do
         (nlg/add-subj clause (nlg/create-noun factory "the" product-name))
         (nlg/add-verb clause rel)
         (nlg/add-obj clause (nlg/concat-multi
                              factory
                              (nlg/create-multi-nouns factory adverb features))))))))
