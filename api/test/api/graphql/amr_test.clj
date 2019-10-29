(ns api.graphql.amr-test
  (:require [api.test-utils :refer [q]]
            [api.graphql.ddb-fixtures :as ddb-fixtures]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each ddb-fixtures/wipe-ddb-tables)

(deftest ^:integration concepts-test
  (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
        _ (q "/_graphql" :post {:query query :variables {:name "written"}})
        _ (q "/_graphql" :post {:query query :variables {:name "provide"}})
        _ (q "/_graphql" :post {:query query :variables {:name "see"}})
        query "{concepts{id concepts{id label roles{id fieldType fieldLabel} dictionaryItem{name phrases{text}} helpText}}}"
        {{{{:keys [id concepts]} :concepts} :data errors :errors} :body} (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (seq concepts))
    (is (= "concepts" id))
    (is (= concepts (sort-by :id (shuffle concepts))))))
