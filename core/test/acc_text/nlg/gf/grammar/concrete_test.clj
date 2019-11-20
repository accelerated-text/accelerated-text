(ns acc-text.nlg.gf.grammar.concrete-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [clojure.test :refer [deftest is]]))

(deftest concrete-grammar
  (is (= #::grammar{:module-name "adjective-phrase-concrete-1"
                    :of          "adjective-phrase"
                    :lin-types   {:data            [:s :str]
                                  :dictionary-item [:s :str]
                                  :document-plan   [:s :str]
                                  :segment         [:s :str]}
                    :lins        [#::grammar{:function-name "document-plan-01"
                                             :syntax        [#::grammar{:role :function :value "segment-02"}]}
                                  #::grammar{:function-name "segment-02"
                                             :syntax        [#::grammar{:role :function :value "dictionary-item-04"}
                                                             #::grammar{:role :function :value "data-03"}]}
                                  #::grammar{:function-name "data-03"
                                             :syntax        [#::grammar{:role :literal :value "{{TITLE}}"}]}
                                  #::grammar{:function-name "dictionary-item-04"
                                             :syntax        [{:role :literal :value "nice"}
                                                             {:role :literal :value "good"}]}]})))
