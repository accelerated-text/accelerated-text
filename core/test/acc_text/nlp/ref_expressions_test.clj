(ns acc-text.nlp.ref-expressions-test
  (:require [clojure.test :refer [deftest is]]
            [acc-text.nlp.ref-expressions :as r]
            [acc-text.nlp.utils :as nlp]))

(deftest test-identify-refs
  (is (= [4 "Alimentum"]
         (->> (nlp/tokenize "Alimentum is nice. Alimentum serves good food. We will eat at Alimentum.")
              (r/identify-potential-refs "Eng")
              (first)))))

(deftest test-replace-refs
  (is (= "Alimentum is nice. It serves good food. Starbucks provides coffee. Its coffee is awesome. We're going to drink coffee at Starbucks."
         (r/apply-ref-expressions "Eng" "Alimentum is nice. It serves good food. Starbucks provides coffee. Starbucks coffee is awesome. We're going to drink coffee at Starbucks."))))

(deftest test-dont-replace-refs-in-unsupported-languages
  (is (= "Alimentum is nice. Alimentum serves good food."
         (r/apply-ref-expressions "Lav" "Alimentum is nice. Alimentum serves good food."))))

(deftest the-it-case
  (is (= "The T1000 is shiny. It makes noise."
         (r/apply-ref-expressions "Eng" "The T1000 is shiny. The T1000 makes noise."))))

(deftest test-test-test-case
  (is (= "TEST TEST TEST."
         (r/apply-ref-expressions "Eng" "TEST TEST TEST"))))

(deftest I-case
  (is (= "I was doing something. Later I was doing something else."
         (r/apply-ref-expressions "Eng" "I was doing something. Later I was doing something else."))))

(deftest alimentum-case
  (is (= "Alimentum is located in city center. It isn't a family friendly coffee shop."
         (r/apply-ref-expressions "Eng" "Alimentum is located in city center. Alimentum isn't a family friendly coffee shop."))))
