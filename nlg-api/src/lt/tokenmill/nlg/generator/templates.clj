(ns lt.tokenmill.nlg.generator.templates
  (:require [lt.tokenmill.nlg.generator.simple-nlg :as nlg]))

(defn product-1
  ;; The <product-name> <has|provides|etc.> <some-adverb> <features-list>
  ;; Example: The Nike Air Max 95 Premium provides exceptional support and comfort
  [args]
  (let [product-name (args :product-name)
        relation (args :relation)
        adverb (args :adverb)
        features (args :features)
        gen (nlg/generator)]
    (gen
     (fn
       [clause factory]
       (do
         (nlg/add-subj clause (nlg/create-noun factory "the" product-name))
         (nlg/add-verb clause relation)
         (nlg/add-obj clause (nlg/concat-multi
                              factory
                              (nlg/create-multi-nouns factory adverb features))))))))

