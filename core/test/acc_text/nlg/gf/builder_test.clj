(ns acc-text.nlg.gf.builder-test
  (:require [acc-text.nlg.gf.builder :as builder]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is]]))

(deftest grammar-building
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
         (builder/build-grammar (utils/load-test-semantic-graph "author-amr-with-adj-default")))))
