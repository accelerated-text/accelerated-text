(ns api.server-test
  (:require [api.db-fixtures :as fixtures]
            [api.test-utils :refer [q]]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as dp]
            [data.entities.user-group :as user-group]))

(use-fixtures :each fixtures/clean-db)

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
    (let [data-id          (data-files/store! {:filename "test.csv" :content "test\n1"} user-group/DUMMY-USER-GROUP-ID)
          document-plan-id (:id (dp/add-document-plan {:uid "test" :name "test" :documentPlan {}} user-group/DUMMY-USER-GROUP-ID "test"))
          {{result-id :resultId} :body status :status}
          (q "/nlg/" :post {:dataId           data-id
                            :documentPlanId   document-plan-id
                            :readerFlagValues {}})]
      (is (= 200 status))
      (is (some? result-id)))))
