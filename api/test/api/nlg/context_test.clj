(ns api.nlg.context-test
  (:require [api.nlg.context :as context]
            [api.db-fixtures :as fixtures]
            [api.nlg.parser :as parser]
            #_[api.test-utils :refer [load-test-document-plan]]
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
    (dict-entity/create-dictionary-item item))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

#_(deftest ^:integration dictionary-building
    (let [document-plan (load-test-document-plan "author-amr-with-adj")
          semantic-graph (parser/document-plan->semantic-graph document-plan)
          keys (context/get-dictionary-item-keys semantic-graph)
          context (context/build-dictionary-context keys ["Eng"])]
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
