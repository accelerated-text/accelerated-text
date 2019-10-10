(ns api.server-test
  (:require [api.test-utils :refer [q]]
            [clojure.test :refer [deftest is testing]]))

(deftest ^:integration server-test
  (testing "GraphQL endpoint test"
    (let [{{{{types :types} :__schema} :data} :body}
          (q "/_graphql" :post {:query "{__schema{types{name}}}"})]
      (is (seq types)))
    (let [{{[{message :message}] :errors} :body status :status}
          (q "/_graphql" :post {:query "bad query"})]
      (is (= 200 status))
      (is (= "Failed to parse GraphQL query." message))))
  (testing "NLG endpoint test"
    (let [{{result-id :resultId} :body status :status}
          (q "/nlg" :post {:dataId           "example-user/books.csv"
                           :documentPlanId   "test"
                           :readerFlagValues {}})]
      (is (= 200 status))
      (is (some? result-id))))
  (testing "NLG results"
    (let [{{result-id :resultId} :body status :status}
          (q "/nlg" :post {:documentPlanId "070cd048-b2b9-4890-bdcc-4420ac449c86"
                           :readerFlagValues {}
                           :dataId "example-user/books.csv"})]
      (is (= 200 status))
      (is (some? result-id)))))
