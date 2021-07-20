(ns api.graphql.reader-model-test
  (:require [api.test-utils :refer [q]]
            [api.db-fixtures :as db-fixtures]
            [clojure.test :refer [deftest is testing use-fixtures]]))

(use-fixtures :each db-fixtures/clean-db)

(deftest ^:integration rw-languages
  (testing "Language creation"
    (let [query "mutation createLanguage($id: Language! $name: String! $flag: String $defaultUsage: Usage!) { createLanguage(id: $id name: $name flag: $flag defaultUsage: $defaultUsage){ id name flag defaultUsage }}"
          {{{{:keys [id name flag defaultUsage]} :createLanguage} :data errors :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id           "Ger"
                                            :name         "German"
                                            :flag         "🇩🇪"
                                            :defaultUsage "NO"}})]
      (is (nil? errors))
      (is (= id "Ger"))
      (is (= name "German"))
      (is (= flag "🇩🇪"))
      (is (= defaultUsage "NO"))))
  (testing "Get and delete language"
    (let [query "query language($id: Language!) { language(id: $id){ id name flag defaultUsage }}"
          {{{{:keys [id name flag defaultUsage]} :language} :data errors :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id "Ger"}})]
      (is (nil? errors))
      (is (= id "Ger"))
      (is (= name "German"))
      (is (= flag "🇩🇪"))
      (is (= defaultUsage "NO")))
    (let [query "mutation deleteLanguage($id: Language!) { deleteLanguage(id: $id) }"
          {{{response :deleteLanguage} :data errors :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id "Ger"}})]
      (is (nil? errors))
      (is (true? response)))
    (let [query "query language($id: Language!) { language(id: $id){ id name flag defaultUsage }}"
          {{[{message :message}] :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id "Ger"}})]
      (is (= "Cannot find language with code `Ger`." message)))))


(deftest ^:integration rw-readers
  (testing "Reader creation"
    (let [query "mutation createReaderFlag($id: ID! $name: String! $flag: String $defaultUsage: Usage!) { createReaderFlag(id: $id name: $name flag: $flag defaultUsage: $defaultUsage){ id name flag defaultUsage }}"
          {{{{:keys [id name flag defaultUsage]} :createReaderFlag} :data errors :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id           "rdr"
                                            :name         "Reader"
                                            :flag         "🏳️"
                                            :defaultUsage "NO"}})]
      (is (nil? errors))
      (is (= id "rdr"))
      (is (= name "Reader"))
      (is (= flag "🏳️"))
      (is (= defaultUsage "NO"))))
  (testing "Get and delete reader flag"
    (let [query "query readerFlag($id: ID!) { readerFlag(id: $id){ id name flag defaultUsage }}"
          {{{{:keys [id name flag defaultUsage]} :readerFlag} :data errors :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id "rdr"}})]
      (is (nil? errors))
      (is (= id "rdr"))
      (is (= name "Reader"))
      (is (= flag "🏳️"))
      (is (= defaultUsage "NO")))
    (let [query "mutation deleteReaderFlag($id: ID!) { deleteReaderFlag(id: $id) }"
          {{{response :deleteReaderFlag} :data errors :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id "rdr"}})]
      (is (nil? errors))
      (is (true? response)))
    (let [query "query readerFlag($id: ID!) { readerFlag(id: $id){ id name flag defaultUsage }}"
          {{[{message :message}] :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:id "rdr"}})]
      (is (= "Cannot find reader flag with id `rdr`." message)))))
