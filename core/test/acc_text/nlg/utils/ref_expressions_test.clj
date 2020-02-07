(ns acc-text.nlg.ref-expressions-test
  (:require [clojure.test :refer [deftest is]]
            [acc-text.nlg.utils.ref-expressions :as r]
            [acc-text.nlg.utils.nlp :as nlp]))


(deftest test-identify-refs
  (is (= [[0 "Alimentum"] [4 "Alimentum"]]
         (-> (nlp/tokenize "Alimentum is nice. Alimentum serves good food.")
             (r/identify-potential-refs)
             (first)))))
