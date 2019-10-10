(ns api.end-to-end-test
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [api.test-utils :refer [q]]
            [data.db.dynamo-ops :as ops]
            [data.entities.document-plan :as dp]
            [clojure.string :as string]))


(defn prepare-environment [f]
  (System/setProperty  "aws.region" "eu-central-1")
  (ops/write! (ops/db-access :blockly)
              "1"
              {:uid        "01"
               :name       "authorship"
               :blocklyXml (slurp "test/resources/blockly/authorship.xml")
               :documentPlan
               {:segments
                [{:children
                  [{:type      "AMR"
                    :srcId     "W}lSg%-S(rQ*nmVp3fFV"
                    :conceptId "author"
                    :dictionaryItem
                    {:name   "author"
                     :type   "Dictionary-item"
                     :srcId  "jirq-V{WgCd+u[sdVWpd"
                     :itemId "VB-author"}
                    :roles
                    [{:name "agent"
                      :children
                      [{:name "authors" :type "Cell" :srcId "uakxT`=!W@8/xV#^orHk"}]}
                     {:name "co-agent"
                      :children
                      [{:name "title" :type "Cell" :srcId "X_CwogT+.Z)N@;Mhz.j/"}]}
                     {:name "theme" :children [nil]}]}]
                  :type  "Segment"
                  :srcId "}0Ci`hF%i?izegwAT[@J"}]
                :type  "Document-plan"
                :srcId "eoPNHZ1PSV{MJBwehL^Z"}}
              true)
  (f)
  (dp/delete-document-plan "1"))

(use-fixtures :each prepare-environment)

(deftest full-document-plan-generation
  (testing "NLG results"
    (let [{{result-id :resultId} :body status :status}
          (q "/nlg" :post {:documentPlanId "1"
                           :readerFlagValues {}
                           :dataId "example-user/books.csv"})]

      (Thread/sleep 1000)
      (is (= 200 status))
      (is (some? result-id))
      (let [generation-results (q (str "/nlg/" result-id) :get nil)]
        (is (not(string/blank?
                 (->> (get-in generation-results [:body :variants])
                      (first) (:children)
                      (first) (:children)
                      (first) (:children)
                      (map :text)
                      (string/join " ")))))))))

