(ns api.graphql.dictionary-test
  (:require [api.test-utils :refer [q]]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.db.dynamo-ops :as ops]
            [api.graphql.ddb-fixtures :as fixtures]
            [data.entities.dictionary :as dict-entity]))

(defn prepare-environment [f]
  (ops/write! (ops/db-access :dictionary-combined)
              "VB-test-phrase"
              {:name         "test-phrase"
               :partOfSpeech "VB"
               :phrases      [{:id    "VB-test-phrase/1"
                               :text  "t1"
                               :flags {:default :YES}}
                              {:id    "VB-test-phrase/2"
                               :text  "t2"
                               :flags {:senior  :YES
                                       :default :NO}}]})
  (f)
  (dict-entity/delete-dictionary-item "VB-test-phrase"))

(use-fixtures :each fixtures/wipe-ddb-tables prepare-environment)

(deftest ^:integration full-query-test
  (let [query "{dictionary{items{name partOfSpeech phrases{id text defaultUsage readerFlagUsage{id usage flag{id name}}}}}}"
        {{{{items :items} :dictionary} :data errors :errors} :body}
        (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (seq items))))

(deftest ^:integration list-dictionary-phrases-test
  (let [query "{dictionary{items{name phrases{text}}}}"
        {{{{items :items} :dictionary} :data errors :errors} :body}
        (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (seq items))
    (let [{phrases :phrases :as test-phrase-item} (first (filter (fn [item] (= "test-phrase" (:name item))) items))]
      (is test-phrase-item)
      (is (= 2 (count phrases))))
    (is (= items (sort-by :name (shuffle items))))))

(deftest ^:integration get-dictionary-item-test
  (let [query "{dictionaryItem(id:\"%s\"){name partOfSpeech phrases{text} concept{id}}}"
        {{{{:keys [name partOfSpeech phrases concept]} :dictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query (format query "VB-test-phrase")})]
    (is (nil? errors))
    (is (= "test-phrase" name))
    (is (= "VB" partOfSpeech))
    (is (= (set [{:text "t1"} {:text "t2"}]) (set phrases)))
    (is (some? (:id concept)))))

(deftest ^:integration create-dict-item-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        {{{{:keys [name partOfSpeech]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test-phrase2", :partOfSpeech "VB"}})]
    (is (nil? errors))
    (is (= "test-phrase2" name))
    (is (= "VB" partOfSpeech))
    (dict-entity/delete-dictionary-item "VB-test-phrase2")))

(deftest ^:integration create-phrase-test
  (let [query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        {{{{:keys [phrases]} :createPhrase} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:dictionaryItemId "VB-test-phrase"
                                                       :text             "t3"
                                                       :defaultUsage     "YES"}})]
    (is (nil? errors))
    (is (seq phrases))
    (is (contains? (set (map :text phrases)) "t3"))))

(deftest ^:integration update-phrase-test
  (let [query "mutation UpdatePhrase($id:ID! $text:String!){updatePhrase(id:$id text:$text){text defaultUsage}}"
        {{{{text :text} :updatePhrase} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id   "VB-test-phrase/2"
                                                       :text "t2-updated"}})]
    (is (nil? errors))
    (is (= "t2-updated" text))))

(deftest ^:integration update-phrase-default-usage-test
  (let [query "mutation UpdatePhraseDefaultUsage($id:ID! $defaultUsage:DefaultUsage!){updatePhraseDefaultUsage(id:$id defaultUsage:$defaultUsage){text defaultUsage}}"
        {{{{:keys [defaultUsage text]} :updatePhraseDefaultUsage} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id           "VB-test-phrase/2"
                                                       :defaultUsage "YES"}})]
    (is (nil? errors))
    (is (= "t2" text))
    (is (= "YES" defaultUsage))))

(deftest ^:integration update-phrase-reader-flag-usage-test
  (let [query "mutation UpdateReaderFlagUsage($id:ID! $usage:Usage!){updateReaderFlagUsage(id:$id usage:$usage){id flag{id name} usage}}"
        {{{{:keys [flag usage id]} :updateReaderFlagUsage} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id    "VB-test-phrase/2/senior"
                                                       :usage "YES"}})]
    (is (nil? errors))
    (is (= {:name "senior" :id "senior"} flag))
    (is (= "YES" usage))
    (is (= "VB-test-phrase/2/senior" id))))

(deftest ^:integration delete-dict-item-test
  (is (some? (dict-entity/get-dictionary-item "VB-test-phrase")))
  (let [query "mutation DeleteDictionaryItem($id:ID!){deleteDictionaryItem(id:$id)}"
        {{{response :deleteDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id "VB-test-phrase"}})]
    (is (nil? errors))
    (is (true? response))
    (is (nil? (dict-entity/get-dictionary-item "VB-test-phrase")))))
