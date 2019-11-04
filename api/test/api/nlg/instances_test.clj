(ns api.nlg.instances-test
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [api.db-fixtures :as fixtures]
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

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration dictionary-item-extraction
  (let [semantic-graph (parser/document-plan->semantic-graph (load-test-document-plan "author-amr-with-adj"))]
    (is (= #{"good" "written"} (instances/get-dictionary-items semantic-graph)))))

(deftest ^:integration dictionary-building
  (let [dictionary-items #{"good" "written"}]
    (is (= {:default {"good"    ["excellent"]
                      "written" ["authored"]}
            :senior  {"good"    ["excellent"]
                      "written" ["authored"]}}
           (instances/build-dictionary dictionary-items [:default :senior])))))

(deftest ^:integration context-adding
  (testing "Dictionary item context adding"
    (let [semantic-graph (parser/document-plan->semantic-graph (load-test-document-plan "author-amr-with-adj"))]
      (is (= #{#::sg{:attributes #::sg{:name           "written"
                                       :reader-profile :default}
                     :id         :03
                     :members    ["authored"]
                     :type       :dictionary-item
                     :value      "written"}
               #::sg{:attributes #::sg{:name           "good"
                                       :reader-profile :default}
                     :id         :05
                     :members    ["excellent"]
                     :type       :dictionary-item
                     :value      "good"}
               #::sg{:attributes #::sg{:name           "written"
                                       :reader-profile :senior}
                     :id         :03
                     :members    ["authored"]
                     :type       :dictionary-item
                     :value      "written"}
               #::sg{:attributes #::sg{:name           "good"
                                       :reader-profile :senior}
                     :id         :05
                     :members    ["excellent"]
                     :type       :dictionary-item
                     :value      "good"}}
             (->> (instances/build-instances semantic-graph [:default :senior])
                  (mapcat ::sg/concepts)
                  (filter #(= (::sg/type %) :dictionary-item))
                  (set)))))))
