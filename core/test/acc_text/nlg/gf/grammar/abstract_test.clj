(ns acc-text.nlg.gf.grammar.abstract-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.grammar.abstract :as abstract-grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]))

(deftest abstract-grammar-building
  (is (= #::grammar{:module-name "adjective-phrase"
                    :flags       {:startcat "Document"}
                    :categories  ["Data" "DictionaryItem" "Document" "Segment"]
                    :functions   [#::grammar{:function-name "Document01"
                                             :arguments     ["Segment"]
                                             :return        "Document"}
                                  #::grammar{:function-name "Segment02"
                                             :arguments     ["Data"]
                                             :return        "Segment"}
                                  #::grammar{:function-name "Data03"
                                             :arguments     ["DictionaryItem"]
                                             :return        "Data"}
                                  #::grammar{:function-name "DictionaryItem04"
                                             :arguments     []
                                             :return        "DictionaryItem"}]}
         (abstract-grammar/build "adjective-phrase" (utils/load-test-semantic-graph "adjective-phrase")))))
