(ns nlg.generator.nlg-test
  (:require [clojure.test :refer :all]
            [nlg.generator.simple-nlg :as nlg]))


(deftest test-simple-nlg-functions
  (testing "Simple object + verb + subject"
    (let [gen (nlg/generator)
          expected "Jim attacks Jill."]
      (is (= expected (gen
                       (fn
                         [clause factory]
                         (do
                           (nlg/add-subj clause "Jim")
                           (nlg/add-obj clause "Jill")
                           (nlg/add-verb clause "attack"))))))))
  ;; -- TODO: add functionallity for specifying tenses (maybe we don't even need that)
  ;; (testing "Simple subject + verb (past tense) + object"
  ;;   (let [gen (nlg/generator)
  ;;         expected "Jim attacked Jill"]
  ;;     (is (= expected (gen
  ;;                      (fn
  ;;                        [clause factory]
  ;;                        (do
  ;;                          (nlg/add-subj clause "Jim")
  ;;                          (nlg/add-obj clause "Jill")
  ;;                          (nlg/add-verb clause "attack"))))))))
  (testing "subject + complement "
    (let [gen (nlg/generator)
          expected "Fizzy Cola is fresh and tasty."]
      (is (= expected (gen
                       (fn
                         [clause factory]
                         (do
                           (nlg/add-subj clause "Fizzy Cola")
                           (nlg/add-complement clause "is fresh and tasty.")))))))))
