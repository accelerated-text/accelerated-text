(ns acc-text.nlg.utils.ref-expressions-test
  (:require [clojure.test :refer [deftest is]]
            [acc-text.nlg.utils.ref-expressions :as r]
            [acc-text.nlg.utils.nlp :as nlp]))


(deftest test-identify-refs
  (is (= [[4 "Alimentum"]]
         (-> (nlp/tokenize "Alimentum is nice. Alimentum serves good food. We will eat at Alimentum.")
             (r/identify-potential-refs)
             (first)))))

(deftest test-replace-refs
  (is (= "Alimentum is nice. It serves good food. Starbucks provides coffee. Its coffee is awesome. We're going to drink coffee at Starbucks."
         (r/apply-ref-expressions :en "Alimentum is nice. It serves good food. Starbucks provides coffee. Starbucks coffee is awesome. We're going to drink coffee at Starbucks."))))
