(ns api.end-to-end-test
  (:require [api.db-fixtures :as fixtures]
            [api.test-utils :refer [q load-test-document-plan rebuild-sentence]]
            [clojure.string :as str]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.document-plan :as dp]
            [data.entities.data-files :as data-files]
            [data.entities.dictionary :as dictionary]))

(defn prepare-environment [f]
  (dictionary/create-dictionary-item {:key          "cut"
                                      :name         "cut"
                                      :phrases      ["cut"]
                                      :partOfSpeech :VB})
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(defn add-document-plan [document-plan-id]
  (:id (dp/add-document-plan {:uid          document-plan-id
                              :name         document-plan-id
                              :documentPlan (load-test-document-plan document-plan-id)}
                             document-plan-id)))

(defn store-data-file [filename]
  (data-files/store!
    {:filename (format "example-user/%s" filename)
     :content  (slurp (format "test/resources/accelerated-text-data-files/example-user/%s" filename))}))

(defn generate [document-plan-id filename]
  (q "/nlg/" :post {:documentPlanId   (add-document-plan document-plan-id)
                    :readerFlagValues {}
                    :dataId           (store-data-file filename)}))

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil) [:body :ready]))
    (Thread/sleep 100)))

(defn get-variants [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil)]
      (set
        (for [{[{segments :children}] :children} (get-in response [:body :variants])]
          (str/join " " (for [{sentence-annotations :children} segments]
                          (rebuild-sentence sentence-annotations))))))))

(deftest ^:integration single-element-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "title-only" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Building Search Applications."} (get-variants result-id)))))

(deftest ^:integration authorship-document-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "authorship" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Building Search Applications is author by Manu Konchady."
             "Manu Konchady is the author of Building Search Applications."} (get-variants result-id)))))

(deftest ^:integration adjective-phrase-document-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "adjective-phrase" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good Building Search Applications."} (get-variants result-id)))))

(deftest ^:integration author-amr-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "author-amr" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Agent is the author of co - Agent."
             "Co - Agent is {{...}} by Agent."} (get-variants result-id)))))

(deftest ^:integration single-quote-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "single-quote" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"This is a very good book: Building Search Applications."} (get-variants result-id)))))

(deftest ^:integration single-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "single-modifier" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good."} (get-variants result-id)))))

(deftest ^:integration complex-amr-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "cut-amr" "carol.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Carol cut envelope to into pieces with knife."} (get-variants result-id)))))

(deftest ^:integration multiple-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "multiple-modifiers" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Noted author Manu Konchady."} (get-variants result-id)))))

(deftest ^:integration sequence-block-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-block" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3."} (get-variants result-id)))))

(deftest ^:integration random-sequence-block-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "random-sequence-block" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3." "1 3 2." "2 1 3." "2 3 1." "3 2 1." "3 1 2."} (get-variants result-id)))))

(deftest ^:integration one-of-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "one-of-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good." "Excellent."} (get-variants result-id)))))

(deftest ^:integration multiple-segments-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "multiple-segments" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Manu Konchady is the author of Building Search Applications. Rarely is so much learning displayed with so much grace and charm."
             "Building Search Applications is written by Manu Konchady. Rarely is so much learning displayed with so much grace and charm."}
           (get-variants result-id)))))

(deftest ^:integration sequence-with-empty-shuffle-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-empty-shuffle" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1."} (get-variants result-id)))))

(deftest ^:integration sequence-with-shuffle-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 3 2." "1 2 3."} (get-variants result-id)))))

(deftest ^:integration sequence-with-shuffle-and-empty-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle-and-empty-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 3 2." "1 2 3."} (get-variants result-id)))))

(deftest ^:integration sequence-with-shuffle-and-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle-and-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3 4." "1 2 3 5." "1 2 4 3." "1 2 5 3." "1 3 2 4." "1 3 2 5."
             "1 3 4 2." "1 3 5 2." "1 4 2 3." "1 4 3 2." "1 5 2 3." "1 5 3 2."} (get-variants result-id)))))

(deftest ^:integration if-equal-condition-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-equal-condition" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book was published in 2008."} (get-variants result-id)))))

(deftest ^:integration if-with-and-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-with-and" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book was published in 2008 and is about Lucene."} (get-variants result-id)))))
