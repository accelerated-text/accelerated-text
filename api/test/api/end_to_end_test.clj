(ns api.end-to-end-test
  (:require [api.db-fixtures :as fixtures]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.document-plan :as dp]
            [data.entities.data-files :as data-files]
            [data.entities.dictionary :as dictionary]))

(defn prepare-environment [f]
  (dictionary/create-dictionary-item {:key          "cut"
                                      :name         "cut"
                                      :phrases      ["cut"]
                                      :partOfSpeech :VB})
  (dictionary/create-dictionary-item {:key          "at-location"
                                      :name         "at-location"
                                      :phrases      ["arena" "place" "venue"]
                                      :partOfSpeech :NOUN})
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(defn add-document-plan [document-plan-id]
  (:id (dp/add-document-plan {:uid          document-plan-id
                              :name         document-plan-id
                              :documentPlan (load-test-document-plan document-plan-id)}
                             document-plan-id)))

(defn store-data-file [filename]
  (data-files/store!
    {:filename filename
     :content  (slurp (format "test/resources/accelerated-text-data-files/%s" filename))}))

(defn generate [document-plan-id filename]
  (q "/nlg/" :post {:documentPlanId   (add-document-plan document-plan-id)
                    :readerFlagValues {}
                    :dataId           (store-data-file filename)}))

(defn generate-bulk [document-plan-id rows]
  (q "/nlg/_bulk/" :post {:documentPlanId   (add-document-plan document-plan-id)
                          :readerFlagValues {}
                          :dataRows         rows}))

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil {:format "raw"}) [:body :ready]))
    (Thread/sleep 100)))

(defn get-variants [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil {:format "raw"})
          variants (get-in response [:body :variants])]
      (into {} (map (fn [item] (let [[k v] item] {(keyword k) (set v)})) variants)))))

(deftest ^:integration single-element-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "title-only" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Building Search Applications."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration authorship-document-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "authorship" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Building Search Applications is author by Manu Konchady."
             "Manu Konchady is the author of Building Search Applications."} (-> result-id
                                                                                 (get-variants)
                                                                                 :sample)))))

(deftest ^:integration authorship-document-plan-bulk-generation
  (let [data {"9780307743657" {:title "The Shinning" :author "Stephen King"}
              "9780575099999" {:title "Horns" :author "Joe Hill"}
              "9780099765219" {:title "Fight Club" :author "Chuck Palahniuk"}}
        {{result-id :resultId} :body status :status} (generate-bulk "authorship" data)]
    (is (= 200 status))
    (is (some? result-id))
    ;; (is (= #{"Building Search Applications is author by Manu Konchady."
    ;;          "Manu Konchady is the author of Building Search Applications."} (get-variants result-id)))
    ))

(deftest ^:integration adjective-phrase-document-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "adjective-phrase" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good Building Search Applications."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration author-amr-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "author-amr" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"{{Agent}} is the author of {{co-Agent}}."
             "{{co-Agent}} is {{...}} by {{Agent}}."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration single-quote-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "single-quote" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"This is a very good book: Building Search Applications."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration single-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "single-modifier" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration complex-amr-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "cut-amr" "carol.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Carol cut envelope into pieces with knife."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration multiple-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "multiple-modifiers" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Noted author Manu Konchady."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration sequence-block-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-block" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration random-sequence-block-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "random-sequence-block" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3." "1 3 2." "2 1 3." "2 3 1." "3 2 1." "3 1 2."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration one-of-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "one-of-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good." "Excellent."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration multiple-segments-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "multiple-segments" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    ;; Spaces before dot - our generation bug at the moment, TODO: fix it
    (is (= #{"Manu Konchady is the author of Building Search Applications . Rarely is so much learning displayed with so much grace and charm."
             "Building Search Applications is written by Manu Konchady . Rarely is so much learning displayed with so much grace and charm."}
           (-> result-id (get-variants) :sample)))))

(deftest ^:integration sequence-with-empty-shuffle-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-empty-shuffle" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration sequence-with-shuffle-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 3 2." "1 2 3."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration sequence-with-shuffle-and-empty-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle-and-empty-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 3 2." "1 2 3."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration sequence-with-shuffle-and-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle-and-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3 4." "1 2 3 5." "1 2 4 3." "1 2 5 3." "1 3 2 4." "1 3 2 5."
             "1 3 4 2." "1 3 5 2." "1 4 2 3." "1 4 3 2." "1 5 2 3." "1 5 3 2."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration if-equal-condition-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-equal-condition" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book was published in 2008."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration if-with-and-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-with-and" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book was published in 2008 and is about Lucene."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration if-not-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-not" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book is about computers."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration if-xor-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-xor" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Either the book is written in English or it is less than 50 pages long."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration variable-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Some text."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration variable-multi-def-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable-multi-def" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"X." "Y."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration variable-undefined-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable-undefined" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{""} (-> result-id (get-variants) :sample)))))

(deftest ^:integration variable-unused-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable-unused" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Some text."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "modifier" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Nice text."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration cell-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "modifier-cell" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Computers book."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration location-amr-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "location-amr" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"In the city centre there is a place Alimentum."
             "In the city centre there is a venue Alimentum."
             "In the city centre there is an arena Alimentum."
             "There is a place in the city centre Alimentum."
             "There is a venue in the city centre Alimentum."
             "There is an Alimentum in the city centre."
             "There is an arena in the city centre Alimentum."} (-> result-id (get-variants) :sample)))))

(deftest ^:integration located-near-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "located-near" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"In the city centre , near the KFC there is a place Alimentum."
             "In the city centre , near the KFC there is a venue Alimentum."
             "In the city centre , near the KFC there is an arena Alimentum."
             "There is a place in the city centre , near the KFC Alimentum."
             "There is a venue in the city centre , near the KFC Alimentum."
             "There is an Alimentum in the city centre , near the KFC."
             "There is an arena in the city centre , near the KFC Alimentum."} (-> result-id (get-variants) :sample)))))
