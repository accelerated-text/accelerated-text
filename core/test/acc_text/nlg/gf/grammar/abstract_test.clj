(ns acc-text.nlg.gf.grammar.abstract-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.grammar.abstract :as abstract-grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]))

(deftest abstract-grammar-building
  (is (= #::grammar{:categories  ["Data" "DictionaryItem" "Document" "Segment"]
                    :flags       {:startcat "Document"}
                    :functions   [#::grammar{:arguments     ["segment"]
                                             :function-name "Document01"
                                             :return        "Document"}
                                  #::grammar{:arguments     ["instance"]
                                             :function-name "Segment02"
                                             :return        "Segment"}
                                  #::grammar{:arguments     ["modifier"]
                                             :function-name "Data03"
                                             :return        "Data"}
                                  #::grammar{:arguments     []
                                             :function-name "DictionaryItem04"
                                             :return        "DictionaryItem"}]
                    :module-name "adjective-phrase"}
         (abstract-grammar/build "adjective-phrase" (utils/load-test-semantic-graph "adjective-phrase"))))
  (is (= #::grammar{:categories  ["AMR" "Data" "DictionaryItem" "Document" "Segment"]
                    :flags       {:startcat "Document"}
                    :functions   [#::grammar{:arguments     ["segment"]
                                             :function-name "Document01"
                                             :return        "Document"}
                                  #::grammar{:arguments     ["instance"]
                                             :function-name "Segment02"
                                             :return        "Segment"}
                                  #::grammar{:arguments     ["function" "ARG0" "ARG1"]
                                             :function-name "AMR03"
                                             :return        "AMR"}
                                  #::grammar{:arguments     []
                                             :function-name "DictionaryItem04"
                                             :return        "DictionaryItem"}
                                  #::grammar{:arguments     ["modifier"]
                                             :function-name "Data05"
                                             :return        "Data"}
                                  #::grammar{:arguments     []
                                             :function-name "DictionaryItem06"
                                             :return        "DictionaryItem"}
                                  #::grammar{:arguments     []
                                             :function-name "Data07"
                                             :return        "Data"}]
                    :module-name "adjective-phrase"}
         (abstract-grammar/build "adjective-phrase" (utils/load-test-semantic-graph "author-amr-with-adj")))))
