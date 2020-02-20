(ns acc-text.nlg.dictionary.morphology-test
  (:require [acc-text.nlg.dictionary.morphology :as m]
            [clojure.test :refer [deftest is testing]]
            [clojure.spec.alpha :as s]))

(deftest word-definition-samples
  (testing "cases of wrong word definition"
    (is (not (s/valid? ::m/word-def #::m{:key "dog"})))
    (is (not (s/valid? ::m/word-def #::m{:key "dog" :language :eng}))))

  (testing "correct word definition"
    ;;absolutely minimal word definition
    (is (s/valid? ::m/word-def #::m{:key "dog"
                                    :pos :n
                                    :language :eng}))
    ;;word definitions with optional parts
    (is (s/valid? ::m/word-def #::m{:key "dog"
                                    :pos :n
                                    :language :eng
                                    :gender :m}))
    (is (s/valid? ::m/word-def #::m{:key "dog"
                                    :definition "Canis lupus familiaris"
                                    :sense [1 2]
                                    :pos :n
                                    :language :eng
                                    :gender :m
                                    :inflections {[:nom :sg] "dog"
                                                  [:nom :pl] "dogs"}}))))

