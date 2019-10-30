(ns api.nlg.instances-test
  (:require [api.graphql.ddb-fixtures :as ddb-fixtures]
            [api.nlg.instances :as instances]
            [api.nlg.parser :as parser]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.test :refer [deftest is testing use-fixtures]]))

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

(deftest context-adding
  (testing "Dictionary item context adding"
    (let [document-plan (parser/parse-document-plan (load-test-document-plan "author-amr-with-adj"))]
      (is (= #{#:acctext.amr{:attributes #:acctext.amr{:name           "written"
                                                       :reader-profile :default}
                             :id         :03
                             :members    ["authored"]
                             :type       :dictionary-item
                             :value      "written"}
               #:acctext.amr{:attributes #:acctext.amr{:name           "good"
                                                       :reader-profile :default}
                             :id         :05
                             :members    ["excellent"]
                             :type       :dictionary-item
                             :value      "good"}
               #:acctext.amr{:attributes #:acctext.amr{:name           "written"
                                                       :reader-profile :senior}
                             :id         :03
                             :members    ["authored"]
                             :type       :dictionary-item
                             :value      "written"}
               #:acctext.amr{:attributes #:acctext.amr{:name           "good"
                                                       :reader-profile :senior}
                             :id         :05
                             :members    ["excellent"]
                             :type       :dictionary-item
                             :value      "good"}}
             (->> (instances/build-instances document-plan [:default :senior])
                  (mapcat :acctext.amr/concepts)
                  (filter #(= (:acctext.amr/type %) :dictionary-item))
                  (set)))))))
