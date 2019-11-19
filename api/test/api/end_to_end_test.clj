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

(defn get-variants [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil)]
      (set
        (for [{[{segments :children}] :children} (get-in response [:body :variants])]
          (str/join " " (for [{sentence-annotations :children} segments]
                          (rebuild-sentence sentence-annotations))))))))

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
    (is (= #{"Building Search Applications."} (get-variants result-id)))))

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
    (is (= #{"Building Search Applications is author by Manu Konchady."
             "Manu Konchady is the author of Building Search Applications."} (get-variants result-id)))))

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
    (is (= #{"Good Building Search Applications."} (get-variants result-id)))))

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
    (is (= #{"Agent is the author of co- Agent."} (get-variants result-id)))))

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
    (is (= #{"This is a very good book: Building Search Applications."} (get-variants result-id)))))

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
    (is (= #{"Good."} (get-variants result-id)))))

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
    (is (= #{"Carol cut envelope to into pieces with knife."} (get-variants result-id)))))

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
    (is (= #{"Noted author Manu Konchady."} (get-variants result-id)))))

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
    (is (= #{"1 2 3."} (get-variants result-id)))))

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
    (is (= #{"1 2 3." "1 3 2." "2 1 3." "2 3 1." "3 2 1." "3 1 2."} (get-variants result-id)))))

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
    (is (= #{"Good." "Excellent."} (get-variants result-id)))))

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
    (is (= #{"Manu Konchady is the author of Building Search Applications. Rarely is so much learning displayed with so much grace and charm."
             "Building Search Applications is written by Manu Konchady. Rarely is so much learning displayed with so much grace and charm."}
           (get-variants result-id)))))

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
    (is (= #{"1."} (get-variants result-id)))))

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
    (is (= #{"1 3 2." "1 2 3."} (get-variants result-id)))))

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
    (is (= #{"1 3 2." "1 2 3."} (get-variants result-id)))))

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
    (is (= #{"1 2 3 4." "1 2 3 5." "1 2 4 3." "1 2 5 3." "1 3 2 4." "1 3 2 5."
             "1 3 4 2." "1 3 5 2." "1 4 2 3." "1 4 3 2." "1 5 2 3." "1 5 3 2."} (get-variants result-id)))))
