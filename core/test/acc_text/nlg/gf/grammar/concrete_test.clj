(ns acc-text.nlg.gf.grammar.concrete-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.grammar.concrete :as concrete-grammar]
            [clojure.test :refer [deftest is]]
            [acc-text.nlg.test-utils :as utils]))

(defn build-concrete-grammar [semantic-graph-id context]
  (concrete-grammar/build
    semantic-graph-id
    (format "%s-concrete-1" semantic-graph-id)
    (utils/load-test-semantic-graph semantic-graph-id)
    context))

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
                                             :syntax        [#::grammar{:role :function :value "data-03"}]}
                                  #::grammar{:function-name "data-03"
                                             :syntax        [#::grammar{:role :function :value "dictionary-item-04"}
                                                             #::grammar{:role :literal :value "{{title}}"}]}
                                  #::grammar{:function-name "dictionary-item-04"
                                             :syntax        [#::grammar{:role :literal :value "good"}
                                                             #::grammar{:role :literal :value "nice"}]}]}
         (build-concrete-grammar "adjective-phrase" {:dictionary {"good" ["nice"]}})))
  (is (= #:acc-text.nlg.gf.grammar{:module-name "author-amr-with-adj-concrete-1"
                                   :of          "author-amr-with-adj"
                                   :lin-types   {:amr             [:s :str]
                                                 :data            [:s :str]
                                                 :dictionary-item [:s :str]
                                                 :document-plan   [:s :str]
                                                 :segment         [:s :str]}
                                   :lins        [#:acc-text.nlg.gf.grammar{:function-name "document-plan-01"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "segment-02"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "segment-02"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "amr-03"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "amr-03"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "data-05"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "is"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "the author of"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "data-07"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "amr-03"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "data-07"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "is"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "dictionary-item-04"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "by"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "data-05"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "dictionary-item-04"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "authored"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "written"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "data-05"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "dictionary-item-06"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "{{authors}}"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "dictionary-item-06"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "good"}
                                                                                           #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "nice"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "data-07"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "{{title}}"}]}]}
         (build-concrete-grammar "author-amr-with-adj" {:amr        {:author {:frames [{:syntax [{:pos :NP :value "Agent"}
                                                                                                 {:pos :LEX :value "is"}
                                                                                                 {:pos :LEX :value "the author of"}
                                                                                                 {:pos :NP :value "co-Agent"}]}
                                                                                       {:syntax [{:pos :NP :value "co-Agent"}
                                                                                                 {:pos :LEX :value "is"}
                                                                                                 {:pos :VERB}
                                                                                                 {:pos :PREP :value "by"}
                                                                                                 {:pos :NP :value "Agent"}]}]}}
                                                        :dictionary {"good"    ["nice"]
                                                                     "written" ["authored"]}})))
  (is (= #:acc-text.nlg.gf.grammar{:module-name "simple-plan-concrete-1"
                                   :of          "simple-plan"
                                   :lin-types   {:data          [:s :str]
                                                 :document-plan [:s :str]
                                                 :segment       [:s :str]}
                                   :lins        [#:acc-text.nlg.gf.grammar{:function-name "document-plan-01"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "segment-02"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "segment-02"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "data-03"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "data-03"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "{{product-name}}"}]}]}
         (build-concrete-grammar "simple-plan" {})))
  (is (= #:acc-text.nlg.gf.grammar{:module-name "single-quote-concrete-1"
                                   :of          "single-quote"
                                   :lin-types   {:document-plan [:s :str]
                                                 :quote         [:s :str]
                                                 :segment       [:s :str]}
                                   :lins        [#:acc-text.nlg.gf.grammar{:function-name "document-plan-01"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "segment-02"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "segment-02"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "quote-03"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "quote-03"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "this is a very good book: {{TITLE}}"}]}]}
         (build-concrete-grammar "single-quote" {})))
  (is (= #:acc-text.nlg.gf.grammar{:module-name "quote-quote-concrete-1"
                                   :of          "quote-quote"
                                   :lin-types   {:document-plan [:s :str]
                                                 :quote         [:s :str]
                                                 :segment       [:s :str]}
                                   :lins        [#:acc-text.nlg.gf.grammar{:function-name "document-plan-01"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "segment-02"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "segment-02"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                                                                     :value "quote-03"}]}
                                                 #:acc-text.nlg.gf.grammar{:function-name "quote-03"
                                                                           :syntax        [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                                                                     :value "He said: \\\"GO!\\\""}]}]}
         (build-concrete-grammar "quote-quote" {}))))
