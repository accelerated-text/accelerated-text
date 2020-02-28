(ns api.nlg.context-test
  (:require [api.nlg.context :as context]
            [api.db-fixtures :as fixtures]
            [api.nlg.parser :as parser]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.dictionary :as dictionary]))

(defn prepare-environment [f]
  (doseq [item [#:acc-text.nlg.dictionary.morphology{:key "good"
                                                     :pos      "A"
                                                     :language "Eng"
                                                     :gender   :m
                                                     :sense    :basic
                                                     :inflections {:nom-sg "good"}}
                #:acc-text.nlg.dictionary.morphology{:key "written"
                                                     :pos      "V"
                                                     :language "Eng"
                                                     :gender   :m
                                                     :sense    :basic
                                                     :tenses   {:present-tense "write"
                                                                :past-participle-tense    "written"}}]]
    (dictionary/create-multilang-dict-item item))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration value-extraction
  (let [document-plan (load-test-document-plan "author-amr-with-adj")
        semantic-graph (parser/document-plan->semantic-graph document-plan)]
    (is (= #{"good" "written"} (context/get-values semantic-graph :dictionary-item)))
    (is (= #{"author"} (context/get-values semantic-graph :amr)))))

(deftest ^:integration dictionary-building
  (let [document-plan (load-test-document-plan "author-amr-with-adj")
        semantic-graph (parser/document-plan->semantic-graph document-plan)
        context (context/build-multilang-dictionary-context semantic-graph {:default true})]
    (is (= ["good"] (->> (get context "good")
                         :Eng
                         :inflections
                         (map #(:inflection/value %))
                         (vec))))
    (is (= ["write", "written"] (->> (get context "written")
                         :Eng
                         :tenses
                         (map #(:tense/value %))
                         (vec))))))
