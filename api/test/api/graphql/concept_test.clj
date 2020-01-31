(ns api.graphql.concept-test
  (:require [api.test-utils :refer [q]]
            [api.db-fixtures :as db]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.amr :as amr]))

(defn prepare-environment [f]
  (amr/initialize)
  (f))

(use-fixtures :each db/clean-db prepare-environment)

(deftest ^:integration concepts-test
  (let [query "{concepts{id concepts{id label roles{id fieldType fieldLabel} helpText}}}"
        {{{{:keys [id concepts]} :concepts} :data errors :errors} :body} (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (seq concepts))
    (is (= "concepts" id))
    (is (= concepts (sort-by :id (shuffle concepts))))))
