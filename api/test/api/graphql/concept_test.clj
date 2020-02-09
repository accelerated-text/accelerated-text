(ns api.graphql.concept-test
  (:require [api.test-utils :refer [q]]
            [api.db-fixtures :as db]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.amr :as amr]))

(defn prepare-environment [f]
  (doseq [[id path] [["author" "test/resources/amr/author.yaml"]
                     ["cut" "test/resources/amr/cut.yaml"]
                     ["see" "test/resources/amr/see.yaml"]]]
    (amr/write-amr (amr/read-amr id (slurp path))))
  (f))

(use-fixtures :each db/clean-db prepare-environment)

(deftest ^:integration get-concept
  (let [query "{concept(id:\"%s\"){id label roles{id fieldType fieldLabel} helpText}}}"
        {{{{:keys [id roles helpText label]} :concept} :data errors :errors} :body} (q "/_graphql" :post {:query (format query "author")})]
    (is (nil? errors))
    (is (= "author" id))
    (is (= "author" label))
    (is (= "X is the author of Y\n\nY is written by X" helpText))
    (is (= [{:id "lexicon" :fieldLabel "lexicon" :fieldType ["Str" "List" "lexicon"]}
            {:id "Agent" :fieldLabel "Agent" :fieldType ["Str" "List" "Agent"]}
            {:id "co-Agent" :fieldLabel "co-Agent" :fieldType ["Str" "List" "co-Agent"]}] roles))))

(deftest ^:integration get-concepts
  (let [query "{concepts{id concepts{id label roles{id fieldType fieldLabel} helpText}}}"
        {{{{:keys [id concepts]} :concepts} :data errors :errors} :body} (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (seq concepts))
    (is (= "concepts" id))))

(deftest ^:integration add-concept
  (let [id "author"
        content (slurp "test/resources/amr/author.yaml")
        query "mutation addConcept($id:String! $content:String!){addConcept(id:$id content:$content){id label roles{id fieldType fieldLabel} helpText}}}"
        {{errors :errors} :body}
        (q "/_graphql" :post {:query     query
                              :variables {:id      id
                                          :content content}})]
    (is (nil? errors))
    (is (= (amr/read-amr id content) (amr/get-amr id)))))

(deftest ^:integration delete-concept
  (let [id "author"
        query "mutation deleteConcept($id:String!){deleteConcept(id:$id)}}"
        {{errors :errors} :body} (q "/_graphql" :post {:query     query
                                                       :variables {:id id}})]
    (is (nil? errors))
    (is (nil? (amr/get-amr id)))))
