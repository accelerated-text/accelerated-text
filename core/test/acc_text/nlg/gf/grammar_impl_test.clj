(ns acc-text.nlg.gf.grammar-impl-test
  (:require [acc-text.nlg.gf.grammar-impl :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]
            [clojure.spec.test.alpha :as stest]))

(stest/instrument `build)

(deftest grammar-building
  (is (= ["Document. S ::= x02;"
          "Segment. x02 ::= x03;"
          "DataMod. x03 ::= x04 \"{{title}}\";"
          "Item. x04 ::= \"excellent\";"
          "Item. x04 ::= \"good\";"]
         (grammar/build (utils/load-test-semantic-graph "adjective-phrase-default"))))
  (is (= ["Document. S ::= x02;"
          "Segment. x02 ::= x03;"
          "AuthorV1. x03 ::= x05 \"is\" \"the author of\" x07;"
          "AuthorV2. x03 ::= x07 \"is\" x04 \"by\" x05;"
          "Item. x04 ::= \"authored\";"
          "Item. x04 ::= \"written\";"
          "DataMod. x05 ::= x06 \"{{authors}}\";"
          "Item. x06 ::= \"excellent\";"
          "Item. x06 ::= \"good\";"
          "Data. x07 ::= \"{{title}}\";"]
         (grammar/build (utils/load-test-semantic-graph "author-amr-with-adj-default"))))
  (is (= ["Document. S ::= x02;"
          "Segment. x02 ::= x03;"
          "Data. x03 ::= \"{{product-name}}\";"]
         (grammar/build (utils/load-test-semantic-graph "simple-plan-default"))))
  (is (= ["Document. S ::= x02;"
          "Segment. x02 ::= x03;"
          "Quote. x03 ::= \"this is a very good book: {{TITLE}}\";"]
         (grammar/build (utils/load-test-semantic-graph "single-quote-default")))))

(deftest quote-cases
  (is (= ["Document. S ::= x02;"
          "Segment. x02 ::= x03;"
          "Quote. x03 ::= \"He said: \\\"GO!\\\"\";"]
         (grammar/build (utils/load-test-semantic-graph "quote-quote")))))
