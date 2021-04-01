(ns acc-text.nlg.core-test
  (:require [acc-text.nlg.core :as core]
            [acc-text.nlg.test-utils :as test-utils]
            [clojure.string :as str]
            [clojure.test :refer [deftest are is testing]]))

(deftest ^:integration multi-language-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "language-test")
        context (test-utils/load-test-context "language-test")]
    (are [lang result] (= result (map :text (core/generate-text semantic-graph context lang)))
                       "Eng" ["There is a text."]
                       "Est" ["On olemas text."]
                       "Ger" ["Es gibt einen text."]
                       "Lav" ["Ir text."]
                       "Rus" ["Существует text."]
                       "Spa" ["Hay un text."])))

(deftest ^:integration list-generation
  (let [context {:data       {"product" "product." "fridge" "fridge."}
                 :dictionary [#:acc-text.nlg.dictionary.item{:id       "03393900-ddbf-426f-b267-f100d09824c0"
                                                             :key      "fridge"
                                                             :category "N"
                                                             :language "Eng"
                                                             :forms    [#:acc-text.nlg.dictionary.item.form{:value "fridge"}
                                                                        #:acc-text.nlg.dictionary.item.form{:value "fridges"}]}
                              #:acc-text.nlg.dictionary.item{:id       "9399a059-f5a5-4443-bee7-a096d284a85d"
                                                             :key      "product"
                                                             :category "N"
                                                             :language "Eng"
                                                             :forms    [#:acc-text.nlg.dictionary.item.form{:value "product"}
                                                                        #:acc-text.nlg.dictionary.item.form{:value "products"}]}]}]
    (testing "Sequences"
      (let [semantic-graph (test-utils/load-test-semantic-graph "sequence-test")]
        (is (= ["Cafe, restaurant and pub. Affordable and average."]
               (map :text (core/generate-text semantic-graph (test-utils/load-test-context "sequence-test") "Eng"))))))
    (testing "Synonyms"
      (let [semantic-graph (test-utils/load-test-semantic-graph "synonyms-test")]
        (is (= #{"Fridge." "Product."}
               (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))
    (testing "Shuffle"
      (let [semantic-graph (test-utils/load-test-semantic-graph "shuffle-test")]
        (is (= #{"Cafe, pub and restaurant." "Cafe, restaurant and pub."
                 "Pub, cafe and restaurant." "Pub, restaurant and cafe."
                 "Restaurant, cafe and pub." "Restaurant, pub and cafe."}
               (into #{} (map :text (core/generate-text semantic-graph {} "Eng")))))))
    (testing "List with different arity"
      (let [semantic-graph (test-utils/load-test-semantic-graph "list-arity-test")]
        (is (= #{"Cat. Cat and house. Cat, house and apple."}
               (into #{} (map :text (core/generate-text semantic-graph {} "Eng")))))))
    (testing "List categories"
      (let [semantic-graph (test-utils/load-test-semantic-graph "list-categories-test")
            context (test-utils/load-test-context "list-categories-test")]
        (is (= (set (list (str/join " " ["Cat and house." "Cat and house." "Nobody and nothing." "Big and friendly."
                                         "Everywhere and somewhere." "Where and when. Always and always."
                                         "House such that there is a cat and such that there is an apple."
                                         "There is a cat and there is an apple."])))
               (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))))

(deftest ^:integration amr-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "amr-test")
        context (test-utils/load-test-context "amr-test")]
    (is (= #{"There is a bill." "There is a door." "There is a fridge." "There is a noise." "There is a product." "There is an interior."
             "There was a bill." "There was a door." "There was a fridge." "There was a noise." "There was a product." "There was an interior."
             "There will be a bill." "There will be a door." "There will be a fridge." "There will be a noise." "There will be a product." "There will be an interior."}
           (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))

(deftest ^:integration amr-combining
  (let [semantic-graph (test-utils/load-test-semantic-graph "amr-combine")
        context (test-utils/load-test-context "amr-combine")]
    (is (= #{"It publishes and it writes."
             "It publishes and there is an author."
             "It writes and it publishes."
             "It writes and there is a publisher."
             "There is a publisher and it writes."
             "There is a publisher and there is an author."
             "There is an author and it publishes."
             "There is an author and there is a publisher."}
           (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))

(deftest ^:integration parallel-edge-test
  (let [semantic-graph (test-utils/load-test-semantic-graph "parallel-edge-test")
        context (test-utils/load-test-context "parallel-edge-test")]
    (is (= ["There is an item."] (map :text (core/generate-text semantic-graph context "Eng"))))))

(deftest ^:integration one-of-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "one-of-with-str")
        context (test-utils/load-test-context "one-of-with-str")]
    (is (= #{"Apple is green." "Apple is red."}
           (set (map :text
                     (core/generate-text
                       semantic-graph context "Eng")))))))

(deftest ^:integration capitalize-test
  (let [semantic-graph (test-utils/load-test-semantic-graph "capitalize-test")
        context (test-utils/load-test-context "capitalize-test")]
    (is (= ["One has one. One has a two.\nOne has a three."] (map :text (core/generate-text semantic-graph context "Eng"))))))

(deftest ^:integration modifier-test
  (let [semantic-graph (test-utils/load-test-semantic-graph "modifier-test")
        context (test-utils/load-test-context "modifier-test")]
    (is (= (->> ["Good eatery" "good chinese eatery" "cafe is nice" "nice cafe"
                 "excellent to find" "it is here that it looks" "to look here" "venue there"
                 "it is here that there is restaurant" "delicious to try" "excellent that eatery is a delicious decent cafe"
                 "it is in family-friendly Alimentum that there is italian food and the beautiful door is made of traditional oriental wood."]
                (str/join " ")
                (vector))
           (map :text (core/generate-text semantic-graph context "Eng"))))))

(deftest ^:integration template-amr
  (let [semantic-graph (test-utils/load-test-semantic-graph "template-amr")
        context (test-utils/load-test-context "template-amr")]
    (is (= ["A house is built to last. An object is built to last."]
           (map :text (core/generate-text semantic-graph context "Eng"))))))

(deftest ^:integration empty-amr-pruning
  (let [semantic-graph (test-utils/load-test-semantic-graph "sequence-with-amrs")
        semantic-graph-with-empty-amr (test-utils/load-test-semantic-graph "sequence-with-amrs-empty")
        context (test-utils/load-test-context "sequence-with-amrs")]
    (is (= ["There is a thing, there is an other thing and there is a third thing."]
           (map :text (core/generate-text semantic-graph context "Eng"))))
    (is (= ["There is a thing and there is an other thing."]
           (map :text (core/generate-text semantic-graph-with-empty-amr context "Eng"))))))

(deftest ^:integration unknown-language-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "unknown-language-test")]
    (is (= ["Test."] (map :text (core/generate-text semantic-graph {} "Eng"))))
    (is (= ["Testas."] (map :text (core/generate-text semantic-graph {} "Lit"))))))

(deftest ^:integration reader-model-test
  (let [semantic-graph (test-utils/load-test-semantic-graph "reader-model-test")]
    (is (= ["Some text... Some text for specific reader..."]
           (map :text (core/generate-text semantic-graph {:readers #{"Dc"}} "Eng"))))))

(deftest ^:integration complex-amr-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "complex-amr-test")
        context (test-utils/load-test-context "complex-amr-test")]
    (is (= ["House on the hill. Cat on the hill. Big house on the hill. Big cat on the hill."]
           (map :text (core/generate-text semantic-graph context "Eng"))))))

(deftest ^:integration missing-data-conditions
  (let [semantic-graph (test-utils/load-test-semantic-graph "missing-data-conditions")]
    (is (= ["1 2."] (map :text (core/generate-text semantic-graph {:data {"a" "1", "b" "2"}} "Eng"))))
    (is (= ["1 3."] (map :text (core/generate-text semantic-graph {:data {"a" "1", "b" "2", "c" "3"}} "Eng"))))))
