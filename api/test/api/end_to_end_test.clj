(ns api.end-to-end-test
  (:require [api.db-fixtures :as fixtures]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.utils :as utils]
            [data.entities.document-plan :as dp]
            [data.entities.data-files :as data-files]
            [data.entities.dictionary :as dictionary]
            [acc-text.nlg.dictionary.morphology :as m]))

(defn prepare-environment [f]
  (doseq [item [{:key "cut" :name "cut" :phrases ["cut"] :partOfSpeech :VB}
                {:key "see" :name "see" :phrases ["see"] :partOfSpeech :VB}
                {:key "place" :name "place" :phrases ["arena" "place" "venue"] :partOfSpeech :NN}
                {:key "written" :name "written" :phrases ["written"] :partOfSpeech :VB}
                {:key "is" :name "is" :phrases ["is"] :partOfSpeech :VB}
                {:key "release" :name "release" :phrases ["publised" "released"] :partOfSpeech :VB}]]
    (dictionary/create-dictionary-item item))

  (doseq [item [#::m{:key "place"
                     :pos      :n
                     :language :eng
                     :gender   :m
                     :senses   [:restaurant]
                     :inflections {:nom-sg "place"
                                   :nom-pl "places"}}
                #::m{:key "place"
                     :pos      :n
                     :language :ger
                     :gender   :m
                     :senses   [:restaurant]
                     :inflections {:nom-sg "platz"
                                   :nom-pl "plätze"}}]]
    (dictionary/create-multilang-dict-item item))
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
                    :readerFlagValues {:English true}
                    :dataId           (store-data-file filename)}))

(defn generate-bulk [document-plan-id rows]
  (q "/nlg/_bulk/" :post {:documentPlanId   (add-document-plan document-plan-id)
                          :readerFlagValues {:English true}
                          :dataRows         rows}))

(defn generate-enriched [document-plan-id filename]
  (q "/nlg/" :post {:documentPlanId   (add-document-plan document-plan-id)
                    :readerFlagValues {:English true}
                    :dataId           (store-data-file filename)
                    :enrich           true}))

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil {:format "raw"}) [:body :ready]))
    (Thread/sleep 100)))

(defn get-variants [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil {:format "raw"})
          variants (get-in response [:body :variants])]
      (into {} (map (fn [item] (let [[k v] item] {(keyword k) (set v)})) variants)))))


(defn get-original-results [result-id]
  (->>
    result-id
    (get-variants)
    :sample
    (map :original)
    (set)))

(defn get-enriched-results [result-id]
  (->>
    result-id
    (get-variants)
    :sample
    (map :enriched)
    (set)))

(deftest ^:integration single-element-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "title-only" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Building Search Applications."} (get-original-results result-id)))))

(deftest ^:integration authorship-document-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "authorship" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Building Search Applications is author by Manu Konchady."
             "Manu Konchady is the author of Building Search Applications."} (get-original-results result-id)))))

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
    (is (= #{"Good Building Search Applications."} (get-original-results result-id)))))

(deftest ^:integration author-amr-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "author-amr" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"{{Agent}} is the author of {{co-Agent}}."
             "{{co-Agent}} is {{lexicon}} by {{Agent}}."} (get-original-results result-id)))))

(deftest ^:integration single-quote-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "single-quote" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"This is a very good book: Building Search Applications."} (get-original-results result-id)))))

(deftest ^:integration single-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "single-modifier" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good."} (get-original-results result-id)))))

(deftest ^:integration complex-amr-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "cut-amr" "carol.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Carol cut envelope into pieces with knife."} (get-original-results result-id)))))

(deftest ^:integration multiple-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "multiple-modifiers" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Noted author Manu Konchady."} (get-original-results result-id)))))

(deftest ^:integration sequence-block-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-block" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3."} (get-original-results result-id)))))

(deftest ^:integration random-sequence-block-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "random-sequence-block" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3." "1 3 2." "2 1 3." "2 3 1." "3 2 1." "3 1 2."} (get-original-results result-id)))))

(deftest ^:integration one-of-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "one-of-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Good." "Excellent."} (get-original-results result-id)))))

(deftest ^:integration multiple-segments-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "multiple-segments" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Manu Konchady is the author of Building Search Applications. Rarely is so much learning displayed with so much grace and charm."
             "Building Search Applications is written by Manu Konchady. Rarely is so much learning displayed with so much grace and charm."}
           (get-original-results result-id)))))

(deftest ^:integration sequence-with-empty-shuffle-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-empty-shuffle" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1."} (get-original-results result-id)))))

(deftest ^:integration sequence-with-shuffle-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 3 2." "1 2 3."} (get-original-results result-id)))))

(deftest ^:integration sequence-with-shuffle-and-empty-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle-and-empty-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 3 2." "1 2 3."} (get-original-results result-id)))))

(deftest ^:integration sequence-with-shuffle-and-synonyms-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "sequence-with-shuffle-and-synonyms" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"1 2 3 4." "1 2 3 5." "1 2 4 3." "1 2 5 3." "1 3 2 4." "1 3 2 5."
             "1 3 4 2." "1 3 5 2." "1 4 2 3." "1 4 3 2." "1 5 2 3." "1 5 3 2."} (get-original-results result-id)))))

(deftest ^:integration if-equal-condition-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-equal-condition" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book was published in 2008."} (get-original-results result-id)))))

(deftest ^:integration if-with-and-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-with-and" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book was published in 2008 and is about Lucene."} (get-original-results result-id)))))

(deftest ^:integration if-not-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-not" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"The book is about computers."} (get-original-results result-id)))))

(deftest ^:integration if-xor-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "if-xor" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Either the book is written in English or it is less than 50 pages long."} (get-original-results result-id)))))

(deftest ^:integration variable-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Some text."} (get-original-results result-id)))))

(deftest ^:integration variable-multi-def-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable-multi-def" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"X." "Y."} (get-original-results result-id)))))

(deftest ^:integration variable-undefined-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable-undefined" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{} (get-original-results result-id)))))

(deftest ^:integration variable-unused-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "variable-unused" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Some text."} (get-original-results result-id)))))

(deftest ^:integration modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "modifier" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Nice text."} (get-original-results result-id)))))

(deftest ^:integration cell-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "modifier-cell" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Computers book."} (get-original-results result-id)))))

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
             "There is an arena in the city centre Alimentum."} (get-original-results result-id)))))

(deftest ^:integration located-near-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "located-near-amr" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"In the city centre, near the KFC there is a place Alimentum."
             "In the city centre, near the KFC there is a venue Alimentum."
             "In the city centre, near the KFC there is an arena Alimentum."
             "There is a place in the city centre, near the KFC Alimentum."
             "There is a venue in the city centre, near the KFC Alimentum."
             "There is an Alimentum in the city centre, near the KFC."
             "There is an arena in the city centre, near the KFC Alimentum."} (get-original-results result-id)))))

(deftest ^:integration gf-amr-modifier-plan-generation
  (let [{{result-id :resultId} :body status :status} (generate "gf-amr-modifier" "books.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (= #{"Old and dirty refrigerator is made of steel."} (get-original-results result-id)))))

(deftest ^:integration enriched-results-generation
  (let [{{result-id :resultId} :body status :status} (generate-enriched "located-enrich" "restaurants.csv")]
    (is (= 200 status))
    (is (some? result-id))
    (is (not= "Restaurant located in city center" (first (get-enriched-results result-id))))))

(deftest ^:integration multilang-dict
  (let [search-results (dictionary/search-multilang-dict "place" :restaurant)]
    (is (= 2 (count search-results)))))
