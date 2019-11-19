(ns acc-text.nlg.gf.grammar.cf-test
  (:require [acc-text.nlg.gf.grammar.cf :as cf-grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]))

(defn build-grammar [semantic-graph-name context]
  (cf-grammar/build (utils/load-test-semantic-graph semantic-graph-name) context))

(deftest grammar-building
  (is (= ["Document. S ::= x02;"
          "x02 ::= x03;"
          "DataMod1. x03 ::= x04 \"{{title}}\";"
          "Item. x04 ::= \"excellent\";"
          "Item. x04 ::= \"good\";"]
         (build-grammar "adjective-phrase-default" {:dictionary {"good" ["excellent"]}})))
  (is (= ["Document. S ::= x02;"
          "x02 ::= x03;"
          "AuthorV1. x03 ::= x05 \"is\" \"the author of\" x07;"
          "AuthorV2. x03 ::= x07 \"is\" x04 \"by\" x05;"
          "Item. x04 ::= \"authored\";"
          "Item. x04 ::= \"written\";"
          "DataMod1. x05 ::= x06 \"{{authors}}\";"
          "Item. x06 ::= \"excellent\";"
          "Item. x06 ::= \"good\";"
          "Data. x07 ::= \"{{title}}\";"]
         (build-grammar
           "author-amr-with-adj-default"
           {:amr        {:author {:frames [{:syntax [{:pos :NP :value "Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :LEX :value "the author of"}
                                                     {:pos :NP :value "co-Agent"}]}
                                           {:syntax [{:pos :NP :value "co-Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :VERB}
                                                     {:pos :PREP :value "by"}
                                                     {:pos :NP :value "Agent"}]}]}}
            :dictionary {"good"    ["excellent"]
                         "written" ["authored"]}})))
  (is (= ["Document. S ::= x02;"
          "x02 ::= x03;"
          "Data. x03 ::= \"{{product-name}}\";"]
         (build-grammar "simple-plan-default" {})))
  (is (= ["Document. S ::= x02;"
          "x02 ::= x03;"
          "Quote. x03 ::= \"this is a very good book: {{TITLE}}\";"]
         (build-grammar "single-quote-default" {}))))

(deftest quote-cases
  (is (= ["Document. S ::= x02;"
          "x02 ::= x03;"
          "Quote. x03 ::= \"He said: \\\"GO!\\\"\";"]
         (build-grammar "quote-quote" {}))))
