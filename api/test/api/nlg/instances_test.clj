(ns api.nlg.instances-test
  (:require [api.graphql.ddb-fixtures :as ddb-fixtures]
            [api.nlg.instances :as instances]
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

(use-fixtures :each ddb-fixtures/wipe-ddb-tables prepare-environment)

(deftest dictionary-item-extraction
  (let [document-plan (parser/parse-document-plan (load-test-document-plan "author-amr-with-adj"))]
    (is (= #{"good" "written"} (instances/get-dictionary-items document-plan)))))

(deftest dictionary-building
  (let [dictionary-items #{"good" "written"}]
    (is (= {:default {"good"    ["excellent"]
                      "written" ["authored"]}
            :senior  {"good"    ["excellent"]
                      "written" ["authored"]}}
           (instances/build-dictionary dictionary-items [:default :senior])))))

(deftest context-building
  (let [document-plan (parser/parse-document-plan (load-test-document-plan "author-amr-with-adj"))]
    (is (= [#:acctext.amr{:reader-profile :default
                          :dictionary     {"written" ["authored"]
                                           "good"    ["excellent"]}}
            #:acctext.amr{:reader-profile :senior
                          :dictionary     {"written" ["authored"]
                                           "good"    ["excellent"]}}]
           (map :acctext.amr/context (instances/build-instances document-plan [:default :senior]))))))
