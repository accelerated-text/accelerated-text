(ns acc-text.nlg.dictionary.morphology-test
  (:require [acc-text.nlg.dictionary.morphology :as m]
            [clojure.test :refer [deftest is testing]]
            [clojure.spec.alpha :as s]))

(deftest word-definition-samples
  (testing "cases of wrong word definition"
    (is (not (s/valid? ::m/word-def #::m{:key "dog"})))
    (is (not (s/valid? ::m/word-def #::m{:key "dog" :language :en}))))

  (testing "correct word definition"
    ;;absolutely minimal word definition
    (is (s/valid? ::m/word-def #::m{:key "dog"
                                    :pos :n
                                    :language :en}))
    ;;word definitions with optional parts
    (is (s/valid? ::m/word-def #::m{:key "dog"
                                    :pos :n
                                    :language :en
                                    :gender :m}))
    (is (s/valid? ::m/word-def #::m{:key "dog"
                                    :pos :n
                                    :language :en
                                    :gender :m
                                    :inflections {[:nom :sg] "dog"
                                                  [:nom :pl] "dogs"}}))))

