(ns api.graphql.dictionary-test
  (:require [api.test-utils :refer [q]]
            [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures testing]]
            [data.entities.dictionary :as dict-entity]
            [clojure.tools.logging :as log]))

(use-fixtures :each fixtures/clean-db)

(deftest ^:integration full-query-test
  (testing "AAAAAAAAAAA>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<"
    (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
         {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
         (q "/_graphql" :post {:query query :variables {:name "test-phrase2", :partOfSpeech "VB"}})]
     (is (nil? errors))

     (let [query "{dictionary{items{name partOfSpeech phrases{id text defaultUsage readerFlagUsage{id usage flag{id name}}}}}}"
           {{{{items :items} :dictionary} :data errors :errors} :body :as resp}
           (q "/_graphql" :post {:query query})]
       (is (nil? errors))
       (is (seq items))))))

(deftest ^:integration create-dict-item-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        {{{{:keys [name partOfSpeech]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test-phrase2", :partOfSpeech "VB"}})]
    (is (nil? errors))
    (is (= "test-phrase2" name))
    (is (= "VB" partOfSpeech))
    (dict-entity/delete-dictionary-item "VB-test-phrase2")))

(deftest ^:integration delete-dict-item-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test-phrase2", :partOfSpeech "VB"}})]
    (is (nil? errors))
    (is (some? (dict-entity/get-dictionary-item id)))

    (let [query "mutation DeleteDictionaryItem($id:ID!){deleteDictionaryItem(id:$id)}"
          {{{response :deleteDictionaryItem} :data errors :errors} :body}
          (q "/_graphql" :post {:query query :variables {:id id}})]
      (is (nil? errors))
      (is (true? response))
      (is (nil? (dict-entity/get-dictionary-item "VB-test-phrase"))))))

(deftest ^:integration create-phrase-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test-phrase2", :partOfSpeech "VB"}})]
    (is (nil? errors))
    (is (some? (dict-entity/get-dictionary-item id)))

    (let [query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
          {{{{:keys [phrases]} :createPhrase} :data errors :errors} :body}
          (q "/_graphql" :post {:query query :variables {:dictionaryItemId id
                                                         :text             "t3"
                                                         :defaultUsage     "YES"}})]
      (is (nil? errors))
      (is (seq phrases))
      (is (contains? (set (map :text phrases)) "t3")))))

(deftest ^:integration get-dictionary-item-test
  (let [create-dictionary-item-query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query create-dictionary-item-query :variables {:name "test-phrase", :partOfSpeech "VB"}})

        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _ (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                         :text             "see"
                                                         :defaultUsage     "YES"
                                                         :readerFlagUsage []}})

        query "{dictionaryItem(id:\"%s\"){name partOfSpeech phrases{text} concept{id}}}"
        {{{{:keys [name partOfSpeech phrases concept]} :dictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query (format query id)})]
    (is (nil? errors))
    (is (= "test-phrase" name))
    (is (= "VB" partOfSpeech))
    (is (= (set [{:text "see"}]) (set phrases)))
    (is (some? (:id concept)))))

(deftest ^:integration list-dictionary-phrases-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test-phrase", :partOfSpeech "VB"}})

        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _ (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                       :text             "see"
                                                                       :defaultUsage     "YES"
                                                                       :readerFlagUsage []}})
        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _ (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                       :text             "see1"
                                                                       :defaultUsage     "YES"
                                                                       :readerFlagUsage []}})]
    (is (nil? errors))
    (is (some? (dict-entity/get-dictionary-item id)))

    (let [query "{dictionary{items{name phrases{text}}}}"
         {{{{items :items} :dictionary} :data errors :errors} :body}
         (q "/_graphql" :post {:query query})]
     (is (nil? errors))
     (is (seq items))
     (let [{phrases :phrases :as test-phrase-item} (first (filter (fn [item] (= "test-phrase" (:name item))) items))]
       (is test-phrase-item)
       (is (= 2 (count phrases))))
     (is (= items (sort-by :name (shuffle items)))))))

(deftest ^:integration update-phrase-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test-phrase", :partOfSpeech "VB"}})

        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        {{{{:keys [phrases]} :createPhrase} :data errors :errors} :body}
        (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                     :text             "see"
                                                                     :defaultUsage     "YES"
                                                                     :readerFlagUsage  []}})

        query "mutation UpdatePhrase($id:ID! $text:String!){updatePhrase(id:$id text:$text){text defaultUsage}}"
        {{{{text :text} :updatePhrase} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id   (-> phrases first :id)
                                                       :text "t2-updated"}})]
    (is (nil? errors))
    (is (= "t2-updated" text))))

(deftest ^:integration update-phrase-default-usage-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test-phrase", :partOfSpeech "VB"}})

        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        {{{{:keys [phrases]} :createPhrase} :data errors :errors} :body}
        (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                     :text             "see"
                                                                     :defaultUsage     "YES"
                                                                     :readerFlagUsage  []}})

        query "mutation UpdatePhraseDefaultUsage($id:ID! $defaultUsage:DefaultUsage!){updatePhraseDefaultUsage(id:$id defaultUsage:$defaultUsage){text defaultUsage}}"
        {{{{:keys [defaultUsage text]} :updatePhraseDefaultUsage} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id           (-> phrases first :id)
                                                       :defaultUsage "YES"}})]
    (is (nil? errors))
    (is (= "see" text))
    (is (= "YES" defaultUsage))))
