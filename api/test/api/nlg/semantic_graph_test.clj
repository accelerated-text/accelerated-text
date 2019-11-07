(ns api.nlg.semantic-graph-test
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [api.nlg.semantic-graph :as semantic-graph]
            [api.db-fixtures :as fixtures]
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
  (let [document-plan (load-test-document-plan "author-amr-with-adj")
        semantic-graph (parser/document-plan->semantic-graph document-plan)]
    (is (= #{"good" "written"} (semantic-graph/get-dictionary-items semantic-graph)))))

(deftest ^:integration dictionary-building
  (let [document-plan (load-test-document-plan "author-amr-with-adj")
        semantic-graph (parser/document-plan->semantic-graph document-plan)]
    (is (= {:default {"good"    ["excellent"]
                      "written" ["authored"]}
            :senior  {"good"    ["excellent"]
                      "written" ["authored"]}}
           (semantic-graph/build-dictionary semantic-graph [:default :senior])))))

(deftest ^:integration instance-id-generation
  (is (= :document-plan-01 (semantic-graph/->instance-id "document-plan-01" nil)))
  (is (= :document-plan-01-default (semantic-graph/->instance-id "document-plan-01" :default)))
  (is (= :default (semantic-graph/->instance-id nil :default))))

(deftest ^:integration instance-building
  (let [document-plan (load-test-document-plan "author-amr-with-adj")
        semantic-graph (parser/document-plan->semantic-graph document-plan)
        instances (semantic-graph/build-instances semantic-graph "test-doc-plan" [:default :senior])]
    (testing "Id"
      (is (= #{:test-doc-plan-default :test-doc-plan-senior} (set (map ::sg/id instances)))))
    (testing "Context"
      (let [context (map ::sg/context instances)]
        (is (= #{"test-doc-plan"} (set (map ::sg/document-plan-id context))))
        (is (= #{{"good" ["excellent"], "written" ["authored"]}} (set (map ::sg/dictionary context))))
        (is (= #{:default :senior} (set (map ::sg/reader-profile context))))))
    (testing "Dictionary item context adding"
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
             (->> instances
                  (map ::sg/graph)
                  (mapcat ::sg/concepts)
                  (filter #(= (::sg/type %) :dictionary-item))
                  (set)))))))
