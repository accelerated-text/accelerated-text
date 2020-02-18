(ns api.graphql.concept-test
  (:require [api.test-utils :refer [q]]
            [api.db-fixtures :as db]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each db/clean-db)

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
