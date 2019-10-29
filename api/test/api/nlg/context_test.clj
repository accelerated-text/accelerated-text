(ns api.nlg.context-test
  (:require [api.graphql.ddb-fixtures :as ddb-fixtures]
            [api.nlg.context :as context]
            [api.nlg.parser :as parser]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each ddb-fixtures/wipe-ddb-tables)

(deftest dictionary-building
  (let [document-plan (parser/parse-document-plan (load-test-document-plan "author-amr-with-adj"))
        query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        _ (q "/_graphql" :post {:query query :variables {:name "good"}})
        _ (q "/_graphql" :post {:query query :variables {:name "written"}})
        query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "good"
                                                         :text             "excellent"
                                                         :defaultUsage     "YES"}})
        _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "written"
                                                         :text             "authored"
                                                         :defaultUsage     "YES"}})]
    (is (= {"good"    ["excellent"]
            "written" ["authored"]} (context/build-dictionary document-plan :default)))))

(deftest context-building
  (let [document-plan (parser/parse-document-plan (load-test-document-plan "author-amr-with-adj"))
        query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        _ (q "/_graphql" :post {:query query :variables {:name "good"}})
        _ (q "/_graphql" :post {:query query :variables {:name "written"}})
        query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "good"
                                                         :text             "excellent"
                                                         :defaultUsage     "YES"}})
        _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "written"
                                                         :text             "authored"
                                                         :defaultUsage     "YES"}})]
    (is (= [#:acctext.amr{:dictionary {"written" ["authored"]
                                       "good"    ["excellent"]}
                          :data       {"authors" "X1" "title" "Y1"}}
            #:acctext.amr{:dictionary {"written" ["authored"]
                                       "good"    ["excellent"]}
                          :data       {"authors" "X2" "title" "Y2"}}]
           (map :acctext.amr/context (context/build-contexts document-plan [{"authors" "X1" "title" "Y1"}
                                                                            {"authors" "X2" "title" "Y2"}] :default))))))
