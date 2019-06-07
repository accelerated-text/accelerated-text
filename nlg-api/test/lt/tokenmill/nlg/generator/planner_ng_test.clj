(ns lt.tokenmill.nlg.generator.planner-ng-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.planner-ng :refer :all]
            [lt.tokenmill.nlg.generator.parser-ng :as parser]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.data :as data]
            [clojure.string :as string]))

(defn load-test-data
  [filename]
  (with-open [r (io/reader (format "test/resources/%s.edn" filename))]
      (edn/read (java.io.PushbackReader. r))))

(defn compare-result
  [expected result]
  (println "Result: " result)
  (let [[l, r, _] (data/diff expected result)]
    (is (= [] (remove nil? l)))
    (is (= [] (remove nil? r)))))

(deftest test-compile-single-node-plan
  (testing "Create a single subject plan"
    (let [document-plan (load-test-data "single-subj")
          compiled (parser/parse-document-plan document-plan {} {})]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [first-segment (first compiled)
            concrete-plan (first first-segment)
            expected {:dynamic [{:name {:cell :product-name :dyn-name "$1"} :attrs {:type :product :source :cell}}]
                      :static []}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan)]
          (is (= expected result)))))))

(deftest plan-with-two-features
    (testing "Create subject with two features"
      (let [document-plan (load-test-data "subj-w-2-features")
            compiled (parser/parse-document-plan document-plan {} {})]
        (is (not (empty? compiled)))
        (is (= 1 (count compiled)))
        (let [first-segment (first compiled)
              concrete-plan (first first-segment)
              expected {:dynamic [{:name {:cell :product-name :dyn-name "$1"} :attrs {:type :product :source :cell}}
                                  {:name {:cell :main-feature :dyn-name "$2"} :attrs {:type :benefit :source :cell}}
                                  {:name {:cell :secondary-feature :dyn-name "$3"} :attrs {:type :benefit :source :cell}}]
                        :static ["provide"]}]
          (println "Concrete plan: " concrete-plan)
          (let [result (build-dp-instance concrete-plan)]
            (compare-result expected result))))))

(deftest plan-with-two-features-and-quote
    (testing "Create subject with two features and quote"
      (let [document-plan (load-test-data "subj-w-2-features-and-quote")
            compiled (parser/parse-document-plan document-plan {} {})]
        (is (not (empty? compiled)))
        (is (= 1 (count compiled)))
        (println compiled)
        (let [first-segment (first compiled)
              concrete-plan (first first-segment)
              expected {:dynamic [{:name {:cell :product-name :dyn-name "$1"} :attrs {:type :product :source :cell}}
                                  {:name {:cell :main-feature :dyn-name "$2"} :attrs {:type :benefit :source :cell}}
                                  {:name {:cell :secondary-feature :dyn-name "$3"} :attrs {:type :benefit :source :cell}}
                                  {:name {:quote "special for you" :dyn-name "$4"} :attrs {:type :benefit :source :quote}}]
                        :static ["provide"]}]
          (println "Concrete plan: " concrete-plan)
          (let [result (build-dp-instance concrete-plan)]
            (compare-result expected result))))))

(deftest plan-with-conditional-if
    (testing "Create plan with conditional"
      (let [document-plan (load-test-data "subj-w-if")
            compiled (parser/parse-document-plan document-plan {} {})]
        (is (not (empty? compiled)))
        (is (= 1 (count compiled)))
        (is (= 2 (count (first compiled))))
        ;; First sentence
        (let [concrete-plan (first (first compiled))
              expected {:dynamic [{:name {:cell :product-name :dyn-name "$1"} :attrs {:type :product :source :cell}}
                                  {:name {:cell :main-feature :dyn-name "$3"} :attrs {:type :benefit :source :cell}}
                                  {:name {:cell :secondary-feature :dyn-name "$4"} :attrs {:type :benefit :source :cell}}]
                        :static ["provide"]}
              result (build-dp-instance concrete-plan)]
          (compare-result expected result))
        ;; Second sentence
        (let [concrete-plan (nth (first compiled) 1)
              expected {:dynamic []
                        :static ["provide"]}
              result (build-dp-instance concrete-plan)]
          (is (= ["results in"] (result :static)))
          (let [gated-var (first (result :dynamic))]
            (is (= {:cell :lacing, :dyn-name "$2"} (gated-var :name)))
            (is (contains? (gated-var :attrs) :gate )))))))

(deftest plan-with-conditional-if-else
  (testing "Create plan with if-else"
    (let [document-plan (load-test-data "subj-w-if-else")
          compiled (parser/parse-document-plan document-plan {} {})]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (is (= 2 (count (first compiled))))
      ;; First sentence - Not interesting
      ;; Second sentence
      (let [concrete-plan (nth (first compiled) 1)
            expected {:dynamic []
                      :static ["provide"]}
            result (build-dp-instance concrete-plan)]
        (is (= ["results in" "results in"] (result :static)))
        (let [gated-var (first (result :dynamic))]
          (is (= {:cell :lacing, :dyn-name "$3"} (gated-var :name)))
          (is (contains? (gated-var :attrs) :gate )))))))

(deftest generate-actual-text
  (testing "Create text with product and two features"
    (let [document-plan (load-test-data "subj-w-2-features")
          data [{:product-name "Nike Air"
                 :main-feature "comfort"
                 :secondary-feature "support"}] 
          result (first (render-dp document-plan data))
          expected #{"Nike Air gives comfort and support"
                     "Nike Air offers comfort and support"
                     "Nike Air provides comfort and support"

                     "Nike Air gives comfort, support"
                     "Nike Air offers comfort, support"
                     "Nike Air provides comfort, support"

                     "Nike Air gives support and comfort"
                     "Nike Air offers support and comfort"
                     "Nike Air provides support and comfort"

                     "Nike Air gives support, comfort"
                     "Nike Air offers support, comfort"
                     "Nike Air provides support, comfort"

                     "Nike Air gives comfort"
                     "Nike Air offers comfort"
                     "Nike Air provides comfort"

                     "Nike Air gives support"
                     "Nike Air offers support"
                     "Nike Air provides support"}]
      (is (contains? expected result))))
  (testing "Create text with product, two features and component with quote"
    (let [document-plan (load-test-data "subj-w-2-features-and-component")
          data [{:product-name "Nike Air"
                 :main-feature "comfort"
                 :secondary-feature "support"
                 :lacing "premium lacing"}] 
          result (first (render-dp document-plan data))
          expected "a snug fit for everyday wear"]
      (is (string/includes? result expected)))))


(deftest generate-complex-examples
  (testing "Create text with if"
    (let [document-plan (load-test-data "subj-w-if-else")
          data [{:product-name "Nike Air"
                 :main-feature "comfort"
                 :secondary-feature "support"
                 :lacing "premium lacing"
                 :style "wonderful"}] 
          result (first (render-dp document-plan data))
          expected "snug fit for everyday wear"]
      (is (string/includes? result expected))))
  (testing "Create text with else"
    (let [document-plan (load-test-data "subj-w-if-else")
          data [{:product-name "Nike Air"
                 :main-feature "comfort"
                 :secondary-feature "support"
                 :lacing "nylon lacing"
                 :style "wonderful"}] 
          result (first (render-dp document-plan data))
          expected "cool looking fit"]
      (is (string/includes? result expected)))))
