(ns acc-text.nlg.core-test
  (:require [acc-text.nlg.core :as core]
            [acc-text.nlg.test-utils :as test-utils]
            [clojure.math.combinatorics :refer [cartesian-product permutations]]
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
  (let [context {:data       {:product "product" :fridge "fridge"}
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
        (is (= (->> "there is a product. there is a fridge." (repeat 6) (str/join " ") (str/capitalize) (vector))
               (map :text (core/generate-text semantic-graph context "Eng"))))))
    (testing "Synonyms"
      (let [semantic-graph (test-utils/load-test-semantic-graph "synonyms-test")]
        (is (= (->> #{"There is a product." "There is a fridge."})
               (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))
    (testing "Shuffle"
      (let [semantic-graph (test-utils/load-test-semantic-graph "shuffle-test")]
        (is (= (->> ["there is a product." "there is a fridge."]
                    (repeat 3)
                    (flatten)
                    (permutations)
                    (map #(str/capitalize (str/join " " %)))
                    (into #{}))
               (into #{} (map :text (core/generate-text semantic-graph context "Eng")))))))))

(deftest ^:integration amr-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "amr-test")
        context (test-utils/load-test-context "amr-test")]
    (is (= (->> (cartesian-product
                  #{"there was a bill." "there will be a bill." "there is a bill."}
                  #{"there is a door." "there was a door." "there will be a door."}
                  #{"there was a product." "there will be a product." "there is a product."}
                  #{"there will be a noise." "there is a noise." "there was a noise."}
                  #{"there was an interior." "there will be an interior." "there is an interior."}
                  #{"there was a fridge." "there is a fridge." "there will be a fridge."})
                (map (comp str/capitalize #(str/join " " %)))
                (into #{}))
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
