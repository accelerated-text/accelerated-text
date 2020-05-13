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
                 :dictionary {["fridge" "N"]  #:acc-text.nlg.dictionary.item{:id       "03393900-ddbf-426f-b267-f100d09824c0"
                                                                             :key      "fridge"
                                                                             :category "N"
                                                                             :language "Eng"
                                                                             :forms    ["fridge" "fridges"]}
                              ["product" "N"] #:acc-text.nlg.dictionary.item{:id       "9399a059-f5a5-4443-bee7-a096d284a85d"
                                                                             :key      "product"
                                                                             :category "N"
                                                                             :language "Eng"
                                                                             :forms    ["product" "products"]}}}]
    (testing "Sequences"
      (let [semantic-graph (test-utils/load-test-semantic-graph "sequence-test")]
        (is (= ["Product fridge product. fridge. There is product. There is fridge. product fridge product. fridge. There is product. There is fridge."]
               (map :text (core/generate-text semantic-graph context "Eng"))))))
    (testing "Synonyms"
      (let [semantic-graph (test-utils/load-test-semantic-graph "synonyms-test")]
        (is (= #{"Fridge." "Product." "There is fridge." "There is product."}
               (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))
    (testing "Shuffle"
      (let [semantic-graph (test-utils/load-test-semantic-graph "shuffle-test")]
        (is (= #{"Product. There is product. product. fridge."
                 "Product. product. There is product. fridge."
                 "There is product. product. product. fridge."}
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
    (is (= ["One has one. One has a two. One has a three."] (map :text (core/generate-text semantic-graph context "Eng"))))))

(deftest ^:integration modifier-test
  (let [semantic-graph (test-utils/load-test-semantic-graph "modifier-test")
        context (test-utils/load-test-context "modifier-test")]
    (is (= (->> ["There is good eatery." "There is good chinese eatery." "Cafe is nice." "There is nice cafe."
                 "Excellent to find." "It is here that it looks." "It looks here." "There is venue there."
                 "It is here that there is restaurant." "Delicious to try." "Excellent that eatery is a delicious decent cafe."
                 "It is in family-friendly Alimentum that there is italian food and the beautiful door is made of traditional oriental wood."]
                (str/join " ")
                (vector))
           (map :text (core/generate-text semantic-graph context "Eng"))))))
