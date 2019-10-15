(ns api.nlg.generator.planner-ng-test
  (:require [api.nlg.generator.planner-ng :as planner]
            [api.nlg.generator.parser-ng :as parser]
            [api.test-utils :refer [load-test-data]]
            [clojure.data :as data]
            [clojure.string :as string]
            [clojure.test :refer [deftest is testing]]
            [clojure.tools.logging :as log]))

(defn compare-result
  [expected result]
  (let [[l r _] (data/diff expected result)]
    (is (= [] (remove nil? l)))
    (is (= [] (remove nil? r)))))

(deftest test-compile-single-node-plan
  (let [compiled (parser/parse-document-plan
                   (load-test-data "document_plans/single-subj")
                   {} {:reader-profile :default})]
    (is (= 1 (count compiled)))
    (is (compare-result
          {:dynamic        [{:name  {:cell :product-name :dyn-name "$1"}
                             :attrs {:type :product :source :cell}}]
           :static         []
           :reader-profile :default}
          (planner/build-dp-instance (-> compiled first first))))))

(deftest plan-with-two-features
  (testing "Create subject with two features"
    (let [document-plan (load-test-data "document_plans/subj-w-2-features")
          compiled (parser/parse-document-plan document-plan {} {:reader-profile :default})]
      (is (seq compiled))
      (is (= 1 (count compiled)))
      (let [first-segment (first compiled)
            concrete-plan (first first-segment)
            expected {:dynamic        [{:name  {:cell :product-name :dyn-name "$1"}
                                        :attrs {:type :product :source :cell}}
                                       {:name  {:cell :main-feature :dyn-name "$2"}
                                        :attrs {:type :benefit :source :cell}}
                                       {:name  {:cell :secondary-feature :dyn-name "$3"}
                                        :attrs {:type :benefit :source :cell}}]
                      :static         ["provides"]
                      :reader-profile :default}]
        (log/debugf "Concrete plan: %s" (pr-str concrete-plan))
        (let [result (planner/build-dp-instance concrete-plan)]
          (log/debugf "Result: %s" (pr-str result))
          (compare-result expected result))))))

(deftest plan-with-two-features-and-quote
  (testing "Create subject with two features and quote"
    (let [document-plan (load-test-data "document_plans/subj-w-2-features-and-quote")
          compiled (parser/parse-document-plan document-plan {} {:reader-profile :default})]
      (is (seq compiled))
      (is (= 1 (count compiled)))
      (let [first-segment (first compiled)
            concrete-plan (first first-segment)
            expected {:dynamic        [{:name  {:cell :product-name :dyn-name "$1"}
                                        :attrs {:type :product :source :cell}}
                                       {:name  {:cell :main-feature :dyn-name "$2"}
                                        :attrs {:type :benefit :source :cell}}
                                       {:name  {:cell :secondary-feature :dyn-name "$3"}
                                        :attrs {:type :benefit :source :cell}}
                                       {:name  {:quote "special for you" :dyn-name "$4"}
                                        :attrs {:type :benefit :source :quote}}]
                      :static         ["provides"]
                      :reader-profile :default}]
        (log/debugf "Concrete plan: %s" (pr-str concrete-plan))
        (let [result (planner/build-dp-instance concrete-plan)]
          (compare-result expected result))))))

(deftest plan-with-conditional-if
  (testing "Create plan with conditional"
    (let [document-plan (load-test-data "document_plans/subj-w-if")
          compiled (parser/parse-document-plan document-plan {} {:reader-profile :default})]
      (is (seq compiled))
      (is (= 1 (count compiled)))
      (is (= 2 (count (first compiled))))
      ;; First sentence
      (let [concrete-plan (ffirst compiled)
            expected {:dynamic        [{:name  {:cell :product-name :dyn-name "$1"}
                                        :attrs {:type :product :source :cell}}
                                       {:name  {:cell :main-feature :dyn-name "$3"}
                                        :attrs {:type :benefit :source :cell}}
                                       {:name  {:cell :secondary-feature :dyn-name "$4"}
                                        :attrs {:type :benefit :source :cell}}]
                      :static         ["provides"]
                      :reader-profile :default}
            result (planner/build-dp-instance concrete-plan)]
        (compare-result expected result))
      ;; Second sentence
      (let [concrete-plan (nth (first compiled) 1)
            result (planner/build-dp-instance concrete-plan)]
        (is (= ["results"] (result :static)))
        (let [gated-var (first (result :dynamic))]
          (is (= {:cell :lacing :dyn-name "$2"} (gated-var :name)))
          (is (contains? (gated-var :attrs) :gate)))))))

(deftest plan-with-conditional-if-else
  (testing "Create plan with if-else"
    (let [document-plan (load-test-data "document_plans/subj-w-if-else")
          compiled (parser/parse-document-plan document-plan {} {:reader-profile :default})]
      (is (seq compiled))
      (is (= 1 (count compiled)))
      (is (= 2 (count (first compiled))))
      ;; First sentence - Not interesting
      ;; Second sentence
      (let [concrete-plan (nth (first compiled) 1)
            result (planner/build-dp-instance concrete-plan)]
        (is (= ["results" "results"] (result :static)))
        (let [gated-var (first (result :dynamic))]
          (is (= {:cell :lacing :dyn-name "$3"} (gated-var :name)))
          (is (contains? (gated-var :attrs) :gate)))))))

(deftest generate-actual-text
  (testing "Create text with product and two features"
    (let [document-plan (load-test-data "document_plans/subj-w-2-features")
          data [{:product-name      "Nike Air"
                 :main-feature      "comfort"
                 :secondary-feature "support"}]
          result (first (planner/render-dp document-plan data :default))
          expected #{"Nike Air gives comfort and support."
                     "Nike Air offers comfort and support."
                     "Nike Air provides comfort and support."

                     "Nike Air gives comfort, support."
                     "Nike Air offers comfort, support."
                     "Nike Air provides comfort, support."

                     "Nike Air gives support and comfort."
                     "Nike Air offers support and comfort."
                     "Nike Air provides support and comfort."

                     "Nike Air gives support, comfort."
                     "Nike Air offers support, comfort."
                     "Nike Air provides support, comfort."

                     "Nike Air gives comfort."
                     "Nike Air offers comfort."
                     "Nike Air provides comfort."

                     "Nike Air gives support."
                     "Nike Air offers support."
                     "Nike Air provides support."}]
      (is (contains? expected result))))
  (testing "Create text with product, two features and component with quote"
    (let [document-plan (load-test-data "document_plans/subj-w-2-features-and-component")
          data [{:product-name      "Nike Air"
                 :main-feature      "comfort"
                 :secondary-feature "support"
                 :lacing            "premium lacing"}]
          result (first (planner/render-dp document-plan data :default))
          expected #{"A snug fit for everyday wear."
                     "Premium lacing results in a snug fit for everyday wear."}]
      (is (contains? expected result)))))

(deftest generate-complex-examples
  (testing "Create text with if"
    (let [document-plan (load-test-data "document_plans/subj-w-if-else")
          data [{:product-name      "Nike Air"
                 :main-feature      "comfort"
                 :secondary-feature "support"
                 :lacing            "premium lacing"
                 :style             "wonderful"}]
          result (first (planner/render-dp document-plan data :default))
          expected "snug fit for everyday wear"]
      (is (string/includes? result expected))))
  (testing "Create text with else"
    (let [document-plan (load-test-data "document_plans/subj-w-if-else")
          data [{:product-name      "Nike Air"
                 :main-feature      "comfort"
                 :secondary-feature "support"
                 :lacing            "nylon lacing"
                 :style             "wonderful"}]
          result (first (planner/render-dp document-plan data :default))
          expected "cool looking fit"]
      (is (string/includes? result expected)))))

(deftest generate-simple-statement
  (is (= "X." (first (planner/render-dp (load-test-data "document_plans/simple-plan")
                                        [{:product-name "X"}] :default)))))

(deftest ^:integration plan-with-dictionary
  (testing "Create text with dictionary"
    (let [document-plan (load-test-data "document_plans/subj-w-dictionary")
          data [{:product-name "Nike Air"
                 :main-feature "comfort"}]
          result (planner/render-dp document-plan data {})]
      (is (seq result)))))

(deftest ^:integration plan-with-amr
  (testing "Create text with amr"
    (let [document-plan (load-test-data "document_plans/subj-w-amr")
          data [{:product-name      "Nike Air"
                 :main-feature      "comfort"
                 :secondary-feature "support"}]
          result (planner/render-dp document-plan data {})]
      (is (seq result))
      (log/debugf "Final AMR results: %s" (pr-str result)))))

(deftest ^:integration plain-plan-with-amr
  (testing "Handle plan with it"
    (let [document-plan (load-test-data "document_plans/plain-amr")
          data [{:actor    "Harry"
                 :co-actor "Sally"}]
          result (planner/render-dp document-plan data {})]
      (is (seq result))
      (log/debugf "Final AMR results: %s" (pr-str result)))))

(deftest ^:integration plain-plan-with-author-amr
  (testing "Handle plan with it"
    (let [document-plan (load-test-data "document_plans/plain-author-amr")
          data [{:actor    "Paul Vigna, Michael J. Casey"
                 :co-actor "The Age of Cryptocurrency"}]
          result (planner/render-dp document-plan data {})]
      (is (seq result))
      (log/debugf "Final AMR results: %s" (pr-str result)))))
