(ns api.graphql.amr-test
  (:require [api.test-utils :refer [with-dev-aws-credentials q]]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :once with-dev-aws-credentials)

(deftest ^:integration concepts-test
  (let [query "{concepts{id concepts{id label roles{id fieldType fieldLabel} dictionaryItem{name phrases{text}} helpText}}}"
        {{{{:keys [id concepts]} :concepts} :data errors :errors} :body} (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (seq concepts))
    (is (= "concepts" id))
    (is (= concepts (sort-by :id (shuffle concepts))))))
