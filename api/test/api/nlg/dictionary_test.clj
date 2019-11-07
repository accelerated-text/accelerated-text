(ns api.nlg.dictionary-test
  (:require [api.db-fixtures :as fixtures]
            [api.nlg.dictionary :as dictionary]
            [api.test-utils :refer [q]]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each fixtures/clean-db)

(deftest ^:integration query-database
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        _ (q "/_graphql" :post {:query query :variables {:name "see"}})
        query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "see"
                                                         :text             "see"
                                                         :defaultUsage     "YES"
                                                         :readerFlagUsage []}})
        _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "see"
                                                         :text             "watch"
                                                         :defaultUsage     "YES"}})
        result (dictionary/search "see" {:junior true :senior true})]
    (is (= #{"see" "watch"} (set result)))))
