(ns acc-text.nlg.gf.grammar.abstract-test
  (:require [acc-text.nlg.gf.grammar :as g]
            [acc-text.nlg.gf.grammar.abstract :as abstract-grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]))

(defn build-grammar [sg-name]
  (abstract-grammar/build sg-name (utils/load-test-semantic-graph sg-name)))

(deftest simple-grammar-building
  (is (= #::g{:categories  #{:document-plan :segment :data}
              :flags       {:startcat :document-plan}
              :functions   [#::g{:arguments     [:segment]
                                 :function-name "document-plan-01"
                                 :return        :document-plan}
                            #::g{:arguments     [:data]
                                 :function-name "segment-segment-02"
                                 :return        :segment}
                            #::g{:arguments     []
                                 :function-name "data-03"
                                 :return        :data}]
              :module-name "simple-plan"}
         (build-grammar "simple-plan"))))

(deftest adj-phrase-grammar-building
  (is (= #::g{:categories  #{:document-plan :segment :data :dictionary-item}
              :flags       {:startcat :document-plan}
              :functions   [#::g{:arguments     [:segment]
                                 :function-name "document-plan-01"
                                 :return        :document-plan}
                            #::g{:arguments     [:data]
                                 :function-name "segment-segment-02"
                                 :return        :segment}
                            #::g{:arguments     [:dictionary-item]
                                 :function-name "instance-data-03"
                                 :return        :data}
                            #::g{:arguments     []
                                 :function-name "data-03"
                                 :return        :data}
                            #::g{:arguments     []
                                 :function-name "dictionary-item-04"
                                 :return        :dictionary-item}]
              :module-name "adjective-phrase"}
         (build-grammar "adjective-phrase"))))

(deftest adj-mod-grammar-building
  (let [{cats ::g/categories
         [dp seg inst-amr arg0-data dict-fun data-auth dict-good data-title]
         ::g/functions } (build-grammar "author-amr-with-adj")]
    (is (= #{:amr :data :dictionary-item :document-plan :segment} cats))
    (is (= #::g{:arguments     [:segment]
                :function-name "document-plan-01"
                :return        :document-plan} dp))
    (is (= #::g{:arguments     [:amr]
                :function-name "segment-segment-02"
                :return        :segment} seg))
    (is (= #::g{:arguments     [:dictionary-item :data :data]
                :function-name "instance-amr-03"
                :return        :amr} inst-amr))
    (is (= #::g{:arguments     [:dictionary-item]
                :function-name "ARG0-data-05"
                :return        :data} arg0-data))
    (is (= #::g{:arguments     []
                :function-name "dictionary-item-04"
                :return        :dictionary-item} dict-fun))
    (is (= #::g{:arguments     []
                :function-name "data-05"
                :return        :data} data-auth))
    (is (= #::g{:arguments     []
                :function-name "dictionary-item-06"
                :return        :dictionary-item} dict-good))
    (is (= #::g{:arguments     []
                :function-name "data-07"
                :return        :data} data-title))))
