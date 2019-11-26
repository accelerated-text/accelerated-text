(ns acc-text.nlg.gf.grammar.abstract-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.grammar.abstract :as abstract-grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]))

(deftest abstract-grammar-building
  (is (= #::grammar{:categories  [:data :dictionary-item :document-plan :segment]
                    :flags       {:startcat :document-plan}
                    :functions   [#::grammar{:arguments     [:segment]
                                             :function-name "document-plan-01"
                                             :return        :document-plan}
                                  #::grammar{:arguments     [:instance]
                                             :function-name "segment-02"
                                             :return        :segment}
                                  #::grammar{:arguments     [:modifier]
                                             :function-name "data-03"
                                             :return        :data}
                                  #::grammar{:arguments     []
                                             :function-name "dictionary-item-04"
                                             :return        :dictionary-item}]
                    :module-name "adjective-phrase"}
         (abstract-grammar/build "adjective-phrase" (utils/load-test-semantic-graph "adjective-phrase"))))
  (is (= #:acc-text.nlg.gf.grammar{:categories  [:amr :data :dictionary-item :document-plan :segment]
                                   :flags       {:startcat :document-plan}
                                   :functions   [#:acc-text.nlg.gf.grammar{:arguments     [:segment]
                                                                           :function-name "document-plan-01"
                                                                           :return        :document-plan}
                                                 #:acc-text.nlg.gf.grammar{:arguments     [:instance]
                                                                           :function-name "segment-02"
                                                                           :return        :segment}
                                                 #:acc-text.nlg.gf.grammar{:arguments     [:function
                                                                                           :ARG0
                                                                                           :ARG1]
                                                                           :function-name "amr-03"
                                                                           :return        :amr}
                                                 #:acc-text.nlg.gf.grammar{:arguments     []
                                                                           :function-name "dictionary-item-04"
                                                                           :return        :dictionary-item}
                                                 #:acc-text.nlg.gf.grammar{:arguments     [:modifier]
                                                                           :function-name "data-05"
                                                                           :return        :data}
                                                 #:acc-text.nlg.gf.grammar{:arguments     []
                                                                           :function-name "dictionary-item-06"
                                                                           :return        :dictionary-item}
                                                 #:acc-text.nlg.gf.grammar{:arguments     []
                                                                           :function-name "data-07"
                                                                           :return        :data}]
                                   :module-name "adjective-phrase"}
         (abstract-grammar/build "adjective-phrase" (utils/load-test-semantic-graph "author-amr-with-adj")))))
