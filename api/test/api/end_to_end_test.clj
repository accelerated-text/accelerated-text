(ns api.end-to-end-test
  (:require [api.db-fixtures :as fixtures]
            [api.test-utils :refer [q load-test-document-plan rebuild-sentence]]
            [clojure.string :as str]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.document-plan :as dp]
            [data.entities.data-files :as data-files]
            [data.entities.dictionary :as dictionary]))

(defn valid-sentence?
  "Test validity of the sentence.
  String is considered a valid sentence if it:
  1. ends with one of the following punctuation symbols - .!?
  2. has at least two alphanumeric characters."
  [txt] (re-matches #".*\w{2,}.*[.?!]" txt))

(defn prepare-environment [f]
  (dictionary/create-dictionary-item {:key          "cut"
                                      :name         "cut"
                                      :phrases      ["cut"]
                                      :partOfSpeech :VB})
  (doseq [id ["title-only"
              "authorship"
              "adjective-phrase"
              "author-amr"
              "single-quote"
              "single-modifier"
              "cut-amr"
              "multiple-modifiers"
              "sequence-block"
              "random-sequence-block"
              "one-of-synonyms"
              "multiple-segments"
              "sequence-with-empty-shuffle"
              "sequence-with-shuffle"
              "sequence-with-shuffle-and-empty-synonyms"
              "sequence-with-shuffle-and-synonyms"]]
    (dp/add-document-plan {:uid          id
                           :name         id
                           :documentPlan (load-test-document-plan id)}
                          id))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil) [:body :ready]))
    (Thread/sleep 100)))

(defn get-first-variant [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil)]
      (str/join " " (for [{sentence-annotations :children} (get-in response [:body :variants 0 :children 0 :children])]
                      (rebuild-sentence sentence-annotations))))))

(deftest ^:integration single-element-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "title-only"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration authorship-document-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "authorship"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration adjective-phrase-document-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "adjective-phrase"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"Building Search Applications." "Good Building Search Applications."}
                   (get-first-variant result-id)))))

(deftest ^:integration author-amr-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "author-amr"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration single-quote-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "single-quote"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "This is a very good book: Building Search Applications." (get-first-variant result-id)))))


(deftest ^:integration single-modifier-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "single-modifier"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "Good." (get-first-variant result-id)))))

(deftest ^:integration complex-amr-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/carol.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/carol.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "cut-amr"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "Carol cut envelope to into pieces with knife." (get-first-variant result-id)))))

(deftest ^:integration multiple-modifier-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "multiple-modifiers"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "Noted author Manu Konchady." (get-first-variant result-id)))))

(deftest ^:integration sequence-block-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "sequence-block"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "1 2 3." (get-first-variant result-id)))))

(deftest ^:integration random-sequence-block-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "random-sequence-block"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"1 2 3." "1 3 2." "2 1 3." "2 3 1." "3 2 1." "3 1 2."} (get-first-variant result-id)))))

(deftest ^:integration one-of-synonyms-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "one-of-synonyms"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"Good." "Excellent."} (get-first-variant result-id)))))

(deftest ^:integration multiple-segments-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "multiple-segments"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"Manu Konchady is the author of Building Search Applications. Rarely is so much learning displayed with so much grace and charm."
                     "Building Search Applications is written by Manu Konchady. Rarely is so much learning displayed with so much grace and charm."}
                   (get-first-variant result-id)))))

(deftest ^:integration sequence-with-empty-shuffle-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "sequence-with-empty-shuffle"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "1." (get-first-variant result-id)))))

(deftest ^:integration sequence-with-shuffle-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "sequence-with-shuffle"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"1 3 2." "1 2 3."} (get-first-variant result-id)))))

(deftest ^:integration sequence-with-shuffle-and-empty-synonyms-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "sequence-with-shuffle-and-empty-synonyms"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"1 3 2." "1 2 3."} (get-first-variant result-id)))))

(deftest ^:integration sequence-with-shuffle-and-synonyms-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "sequence-with-shuffle-and-synonyms"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"1 2 3 4."} (get-first-variant result-id)))))
