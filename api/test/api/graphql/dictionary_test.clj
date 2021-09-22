(ns api.graphql.dictionary-test
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [api.test-utils :refer [q]]
            [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.dictionary :as dict-entity]
            [data.entities.user-group :as user-group]
            [api.utils :as utils]))

(def test-dictionary-items #{#::dict-item{:id       "place_Eng"
                                          :key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Eng"
                                          :forms    [#::dict-item-form{:id (utils/gen-uuid) :value "place"}
                                                     #::dict-item-form{:id (utils/gen-uuid) :value "places"}]}
                             #::dict-item{:id       "place_Ger"
                                          :key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Ger"
                                          :forms    [#::dict-item-form{:id (utils/gen-uuid) :value "platz"}
                                                     #::dict-item-form{:id (utils/gen-uuid) :value "plätze"}]}})

(defn prepare-environment [f]
  (doseq [item test-dictionary-items]
    (dict-entity/create-dictionary-item item user-group/DUMMY-USER-GROUP-ID))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration query-dict-items
  (let [query "{dictionary{items{name partOfSpeech phrases{id text defaultUsage readerFlagUsage{id usage flag{id name}}}}}}"
        {{{{items :items} :dictionary} :data errors :errors} :body}
        (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (< 0 (count items)))))

(deftest ^:integration full-query-test
  (let [query "{dictionary{items{name partOfSpeech phrases{id text defaultUsage readerFlagUsage{id usage flag{id name}}}}}}"
        {{{{items :items} :dictionary} :data errors :errors} :body}
        (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (seq items))))

(deftest ^:integration create-dict-item-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        {{{{:keys [name partOfSpeech]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "test", :partOfSpeech "V"}})]
    (is (nil? errors))
    (is (= "test_V" name))
    (is (= "V" partOfSpeech))))

(deftest ^:integration create-complex-dict-item-test
  (let [query "mutation CreateDictionaryItem($name: String!, $partOfSpeech: PartOfSpeech, $key: String, $forms: [String], $language: Language, $sense: String, $definition: String, $attributes: [Attribute]) { createDictionaryItem(name: $name, partOfSpeech: $partOfSpeech, key: $key, forms: $forms, language: $language, sense: $sense, definition: $definition, attributes: $attributes) { name partOfSpeech language sense definition phrases { id text } attributes { id name value } } } "
        {{{{:keys [name partOfSpeech language sense definition phrases attributes]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name         "test"
                                                       :partOfSpeech "N"
                                                       :key          "test_key"
                                                       :language     "Eng"
                                                       :sense        "test"
                                                       :definition   "test"
                                                       :forms        ["test" "tests"]
                                                       :attributes   [{:name  "Gender"
                                                                       :value "nonhuman"}]}})]
    (is (nil? errors))
    (is (= "test_key" name))
    (is (= "N" partOfSpeech))
    (is (= language "Eng"))
    (is (= sense "test"))
    (is (= definition "test"))
    (is (= ["test" "tests"] (map :text phrases)))
    (is (= {"Gender" "nonhuman"} (into {} (map (fn [{:keys [name value]}] [name value]) attributes))))))

(deftest ^:integration delete-dict-item-test
  (let [query "mutation DeleteDictionaryItem($id:ID!){deleteDictionaryItem(id:$id)}"
        {{{response :deleteDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id "place_Eng"}})]
    (is (nil? errors))
    (is (true? response))
    (is (nil? (dict-entity/get-dictionary-item "place_Eng")))))

(deftest ^:integration create-phrase-test
  (let [query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        {{{{:keys [phrases]} :createPhrase} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:dictionaryItemId "place_Eng"
                                                       :text             "t3"
                                                       :defaultUsage     "YES"}})]
    (is (nil? errors))
    (is (seq phrases))
    (is (contains? (set (map :text phrases)) "t3"))))

(deftest ^:integration get-dictionary-item-test
  (let [dict-item-id                 (utils/gen-uuid)
        create-dictionary-item-query "mutation CreateDictionaryItem($id: ID, $name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(id: $id name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        _                            (q "/_graphql" :post {:query create-dictionary-item-query :variables {:id dict-item-id :name "see", :partOfSpeech "V"}})

        create-phrase-query          "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _                            (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId dict-item-id
                                                                                                  :text             "saw"
                                                                                                  :defaultUsage     "YES"
                                                                                                  :readerFlagUsage  []}})

        query                        "{dictionaryItem(id:\"%s\"){id name partOfSpeech phrases{text} concept{id}}}"
        {{{{:keys [id name partOfSpeech phrases]} :dictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query (format query dict-item-id)})]
    (is (nil? errors))
    (is (= dict-item-id id))
    (is (= "see_V" name))
    (is (= "V" partOfSpeech))
    (is (= #{{:text "see"} {:text "saw"}} (set phrases)))))

(deftest ^:integration list-dictionary-phrases-test
  (let [query               "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:name "see", :partOfSpeech "V"}})

        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _                   (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                                         :text             "saw"
                                                                                         :defaultUsage     "YES"
                                                                                         :readerFlagUsage  []}})
        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        _                   (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                                         :text             "seen"
                                                                                         :defaultUsage     "YES"
                                                                                         :readerFlagUsage  []}})]
    (is (nil? errors))
    (is (some? (dict-entity/get-dictionary-item id)))

    (let [query "{dictionary{items{name phrases{text}}}}"
          {{{{items :items} :dictionary} :data errors :errors} :body}
          (q "/_graphql" :post {:query query})]
      (is (nil? errors))
      (is (seq items))
      (let [{phrases :phrases :as test-phrase-item} (first (filter (fn [item] (= "see_V" (:name item))) items))]
        (is test-phrase-item)
        (is (= 3 (count phrases)))))))

(deftest ^:integration update-phrase-test
  (let [query               "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data} :body}
        (q "/_graphql" :post {:query query :variables {:name "see", :partOfSpeech "V"}})

        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        {{{{:keys [phrases]} :createPhrase} :data} :body}
        (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                     :text             "see"
                                                                     :defaultUsage     "YES"
                                                                     :readerFlagUsage  []}})

        query               "mutation UpdatePhrase($id:ID! $text:String!){updatePhrase(id:$id text:$text){text defaultUsage}}"
        {{{{text :text} :updatePhrase} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id   (-> phrases second :id)
                                                       :text "saw"}})]
    (is (nil? errors))
    (is (= "saw" text))))

(deftest ^:integration update-phrase-default-usage-test
  (let [query               "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech id}}"
        {{{{:keys [id]} :createDictionaryItem} :data} :body}
        (q "/_graphql" :post {:query query :variables {:name "see", :partOfSpeech "V"}})

        create-phrase-query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
        {{{{:keys [phrases]} :createPhrase} :data} :body}
        (q "/_graphql" :post {:query create-phrase-query :variables {:dictionaryItemId id
                                                                     :text             "saw"
                                                                     :defaultUsage     "YES"
                                                                     :readerFlagUsage  []}})

        query               "mutation UpdatePhraseDefaultUsage($id:ID! $defaultUsage:DefaultUsage!){updatePhraseDefaultUsage(id:$id defaultUsage:$defaultUsage){text defaultUsage}}"
        {{{{:keys [defaultUsage text]} :updatePhraseDefaultUsage} :data errors :errors} :body}
        (q "/_graphql" :post {:query query :variables {:id           (-> phrases first :id)
                                                       :defaultUsage "YES"}})]
    (is (nil? errors))
    (is (= "see" text))
    (is (= "YES" defaultUsage))))
