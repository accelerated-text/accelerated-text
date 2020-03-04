(ns api.nlg.context-test
  (:require [api.nlg.context :as context]
            [api.db-fixtures :as fixtures]
            [api.nlg.parser :as parser]
            [api.test-utils :refer [load-test-document-plan]]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.dictionary :as dict-entity]))

(defn prepare-environment [f]
  (doseq [item [#:acc-text.nlg.dictionary.item{:id       "good_1"
                                               :key      "good"
                                               :category "A"
                                               :language "Eng"
                                               :forms    ["good" "better" "best" "well"]}
                #:acc-text.nlg.dictionary.item{:id       "written_1"
                                               :key      "written"
                                               :category "V2"
                                               :language "Eng"
                                               :forms    ["write" "wrote" "written"]}]]
    (dict-entity/write-item item))
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
        context (context/build-dictionary-context semantic-graph ["Eng"])]
    (is (= {"good"    #:acc-text.nlg.dictionary.item{:id       "good_1"
                                                     :key      "good"
                                                     :category "A"
                                                     :language "Eng"
                                                     :forms    ["good" "better" "best" "well"]}
            "written" #:acc-text.nlg.dictionary.item{:id       "written_1"
                                                     :key      "written"
                                                     :category "V2"
                                                     :language "Eng"
                                                     :forms    ["write" "wrote" "written"]}}
           (get-in context ["Eng"])))))
