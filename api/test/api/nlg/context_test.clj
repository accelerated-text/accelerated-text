(ns api.nlg.context-test
  (:require [api.nlg.context :as context]
            [api.db-fixtures :as fixtures]
            [api.nlg.parser :as parser]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.test :refer [deftest is use-fixtures]]))

(defn prepare-environment [f]
  (let [create-dict-item-query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"]
    (q "/_graphql" :post {:query create-dict-item-query :variables {:name "good"}})
    (q "/_graphql" :post {:query create-dict-item-query :variables {:name "written"}})
    (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId "good"
                                                                 :text             "excellent"
                                                                 :defaultUsage     "YES"}})
    (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId "written"
                                                                 :text             "authored"
                                                                 :defaultUsage     "YES"}})
    (f)))

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration value-extraction
  (let [document-plan (load-test-document-plan "author-amr-with-adj")
        semantic-graph (parser/document-plan->semantic-graph document-plan)]
    (is (= #{"good" "written"} (context/get-values semantic-graph :dictionary-item)))
    (is (= #{"author"} (context/get-values semantic-graph :amr)))))

(deftest ^:integration dictionary-building
  (let [document-plan (load-test-document-plan "author-amr-with-adj")
        semantic-graph (parser/document-plan->semantic-graph document-plan)]
    (is (= {"good"    ["excellent"]
            "written" ["authored"]} (context/build-dictionary-context semantic-graph {:default true})))))
