(ns acc-text.nlg.gf.generator-test
  (:require [acc-text.nlg.gf.generator :refer [generate]]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build `generate)

(deftest ^:integration quote-cases
  (is (= ["He said: \"GO!\"."] (let [semantic-graph (utils/load-test-semantic-graph "quote")
                                    grammar (grammar/build "Default" "Instance" semantic-graph {})]
                                (generate grammar)))))

(deftest ^:integration at-location
  (is (= ["in the city centre there is a place Alimentum."
          "in the city centre there is a venue Alimentum."
          "in the city centre there is an arena Alimentum."
          "there is a place in the city centre Alimentum."
          "there is a venue in the city centre Alimentum."
          "there is an Alimentum in the city centre."
          "there is an arena in the city centre Alimentum."]
         (generate (grammar/build "AtLoc" "1" (utils/load-test-semantic-graph "location-amr")
                                  {:amr        {"at-location"
                                                {:frames [{:syntax [{:type   :oper
                                                                     :value  "atLocation"
                                                                     :ret    "S"
                                                                     :params [{:role "lexicon" :type "N"}
                                                                              {:role "locationData" :type "N"}
                                                                              {:role "objectRef" :type "N"}]}]}]}}
                                   :dictionary {"place" ["arena" "place" "venue"]}})))))

(deftest ^:integration polarity
  (is (= ["KFC is family-friendly."]
         (generate (grammar/build "HasProperty" "Pos" (utils/load-test-semantic-graph "has-property")
                                  {:amr  {"has-property"
                                          {:frames [{:syntax [{:type   :oper
                                                               :value  "hasProperty"
                                                               :ret    "S"
                                                               :params [{:role "object" :type "N"}
                                                                        {:role "property" :type "A"}
                                                                        {:role "polarity" :type "Pol"}]}]}]}}
                                   :data {:name           "KFC"
                                          :familyFriendly "true"}}))))
  (is (= ["KFC isn't family-friendly."]
         (generate (grammar/build "HasProperty" "Neg" (utils/load-test-semantic-graph "has-property")
                                  {:amr  {"has-property"
                                          {:frames [{:syntax [{:type   :oper
                                                               :value  "hasProperty"
                                                               :ret    "S"
                                                               :params [{:role "object" :type "N"}
                                                                        {:role "property" :type "A"}
                                                                        {:role "polarity" :type "Pol"}]}]}]}}
                                   :data {:name           "KFC"
                                          :familyFriendly "false"}})))))

(deftest ^:integration nested-amr
  (let [ctx {:amr        {"has-a"
                          {:frames
                           [{:syntax [{:ret    "S" :value "hasA_S"
                                       :params [{:type "CN" :role "Subject"}
                                                {:type "CN" :role "Object"}]
                                       :type   :oper}]}
                            {:syntax [{:ret    "NP" :value "hasA_NP"
                                       :params [{:type "CN" :role "Subject"}
                                                {:type "CN" :role "Object"}]
                                       :type   :oper}]}]}
                          "capable-of"
                          {:frames
                           [{:syntax
                             [{:ret    "S" :value "capableOf"
                               :params [{:type "NP" :role "Subject"}
                                        {:type "V2" :role "Verb"}
                                        {:type "CN" :role "Object"}]
                               :type   :oper}]}]}}
             :data       {:Make "T1000" :Type "power"}
             :dictionary {"NN-boil" ["boil"]}}]
    (is (= ["a T1000 with a power boils water."]
           (generate
             (grammar/build "Nested" "AMR"
                            (utils/load-test-semantic-graph "nested-amr")
                            ctx))))))
