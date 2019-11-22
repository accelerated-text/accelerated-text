(ns acc-text.nlg.gf.grammar.abstract-test
  (:require [acc-text.nlg.gf.grammar :as g]
            [acc-text.nlg.gf.grammar.abstract :as abstract-grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]
            [clojure.spec.test.alpha :as stest]))

;;FIXME now check fails because SG spec allows empty relations
;;(stest/check `abstract-grammar/build)

(stest/instrument `abstract-grammar/build)


(defn build-grammar [sg-name]
  (abstract-grammar/build sg-name (utils/load-test-semantic-graph sg-name)))

(deftest simple-grammar-building
  (is (= #::g{:categories  #{:document-plan :segment-segment :instance-data}
              :flags       {:startcat :document-plan}
              :functions   [#::g{:arguments     [:segment-segment]
                                 :function-name "document-plan-01"
                                 :return        :document-plan}
                            #::g{:arguments     [:instance-data]
                                 :function-name "segment-segment-02"
                                 :return        :segment-segment}
                            #::g{:arguments     []
                                 :function-name "data-03"
                                 :return        :instance-data}]
              :module-name "simple-plan"}
         (build-grammar "simple-plan"))))

(deftest adj-phrase-grammar-building
  (let [{cats ::g/categories
         [dp seg instance-data data mod-dict] ::g/functions} (build-grammar "adjective-phrase")]
    (is (= #{:instance-data :modifier-dictionary-item
             :segment-segment :document-plan} cats))
    (is (= #::g{:arguments     [:segment-segment]
                :function-name "document-plan-01"
                :return        :document-plan} dp))
    (is (= #::g{:arguments     [:instance-data]
                :function-name "segment-segment-02"
                :return        :segment-segment} seg))
    (is (= #::g{:arguments     [:modifier-dictionary-item]
                :function-name "instance-data-03"
                :return        :instance-data} instance-data))
    (is (= #::g{:arguments     []
                :function-name "data-03"
                :return        :instance-data} data))
    (is (= #::g{:arguments     []
                :function-name "dictionary-item-04"
                :return        :modifier-dictionary-item} mod-dict))))

(deftest adj-mod-grammar-building
  (let [{cats ::g/categories
         [dp seg inst-amr arg0-data dict-fun data-auth dict-good data-title]
         ::g/functions} (build-grammar "author-amr-with-adj")]
    (is (= #{:instance-amr :modifier-dictionary-item :segment-segment :ARG1-data
             :ARG0-data :document-plan :function-dictionary-item} cats))
    (is (= #::g{:arguments     [:segment-segment]
                :function-name "document-plan-01"
                :return        :document-plan} dp))
    (is (= #::g{:arguments     [:instance-amr]
                :function-name "segment-segment-02"
                :return        :segment-segment} seg))
    (is (= #::g{:arguments     [:function-dictionary-item :ARG0-data :ARG1-data]
                :function-name "instance-amr-03"
                :return        :instance-amr} inst-amr))
    (is (= #::g{:arguments     [:modifier-dictionary-item]
                :function-name "ARG0-data-05"
                :return        :ARG0-data} arg0-data))
    (is (= #::g{:arguments     []
                :function-name "dictionary-item-04"
                :return        :function-dictionary-item} dict-fun))
    (is (= #::g{:arguments     []
                :function-name "data-05"
                :return        :ARG0-data} data-auth))
    (is (= #::g{:arguments     []
                :function-name "dictionary-item-06"
                :return        :modifier-dictionary-item} dict-good))
    (is (= #::g{:arguments     []
                :function-name "data-07"
                :return        :ARG1-data} data-title))))
