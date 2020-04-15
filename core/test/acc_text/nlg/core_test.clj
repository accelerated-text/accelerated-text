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
                 :dictionary {"fridge"  #:acc-text.nlg.dictionary.item{:id       "03393900-ddbf-426f-b267-f100d09824c0"
                                                                       :key      "fridge"
                                                                       :category "N"
                                                                       :language "Eng"
                                                                       :forms    ["fridge" "fridges"]}
                              "product" #:acc-text.nlg.dictionary.item{:id       "9399a059-f5a5-4443-bee7-a096d284a85d"
                                                                       :key      "product"
                                                                       :category "N"
                                                                       :language "Eng"
                                                                       :forms    ["product" "products"]}}}]
    (testing "Sequences"
      (let [semantic-graph (test-utils/load-test-semantic-graph "sequence-test")]
        (is (= ["Product fridge product. fridge. there is product. there is fridge. product fridge product. fridge. there is product. there is fridge."]
               (map :text (core/generate-text semantic-graph context "Eng"))))))
    (testing "Synonyms"
      (let [semantic-graph (test-utils/load-test-semantic-graph "synonyms-test")]
        (is (= #{"Fridge." "Product." "There is fridge." "There is product."}
               (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))
    (testing "Shuffle"
      (let [semantic-graph (test-utils/load-test-semantic-graph "shuffle-test")]
        (is (= #{"Product. product. there is product. fridge."
                 "Product. there is product. product. fridge."
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

(deftest ^:integration modifier-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "modifier-test")
        context (test-utils/load-test-context "modifier-test")]
    (is (= (->> ["there is a quiet fridge."
                 "there is a cheap kettle."
                 "there is a quiet kettle."
                 "there is a cheap fridge."
                 "there is a quiet fan."
                 "there is a cheap lamp."]
                (str/join " ")
                (str/capitalize))
           (first (map :text (core/generate-text semantic-graph context "Eng")))))))
