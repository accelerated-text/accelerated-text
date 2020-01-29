(ns acc-text.nlg.gf.generator-test
  (:require [acc-text.nlg.gf.generator :refer [generate]]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build `generate)

(deftest ^:integration quote-cases
  (is (= ["He said: \"GO!\""] (let [semantic-graph (utils/load-test-semantic-graph "quote")
                                    grammar (grammar/build "Default" "Instance" semantic-graph {})]
                                (generate grammar)))))

(deftest ^:integration at-location
  (is (= ["in the city centre there is a place Alimentum"
          "in the city centre there is a venue Alimentum"
          "in the city centre there is an arena Alimentum"
          "there is a place in the city centre Alimentum"
          "there is a venue in the city centre Alimentum"
          "there is an Alimentum in the city centre"
          "there is an arena in the city centre Alimentum"]
         (generate (grammar/build "AtLoc" "1" (utils/load-test-semantic-graph "location-amr")
                                  {:amr        {:at-location
                                                {:frames [{:syntax [{:type  :oper
                                                                     :value "atLocation"
                                                                     :roles ["lexicon" "objectRef" "locationData"]
                                                                     :ret   ["N" "N" "N"]}]}]}}
                                   :dictionary {"place" ["arena" "place" "venue"]}})))))

(deftest ^:integration polarity
  (is (= ["KFC is family-friendly"]
         (generate (grammar/build "HasProperty" "Pos" (utils/load-test-semantic-graph "has-property")
                                  {:amr  {:has-property
                                          {:frames [{:syntax [{:type  :oper
                                                               :value "hasProperty"
                                                               :roles ["object" "property" "polarity"]
                                                               :ret   ["N" "A" "Pol"]}]}]}}
                                   :data {:name           "KFC"
                                          :familyFriendly "true"}}))))
  (is (= ["KFC isn't family-friendly"]
         (generate (grammar/build "HasProperty" "Neg" (utils/load-test-semantic-graph "has-property")
                                  {:amr  {:has-property
                                          {:frames [{:syntax [{:type  :oper
                                                               :value "hasProperty"
                                                               :roles ["object" "property" "polarity"]
                                                               :ret   ["N" "A" "Pol"]}]}]}}
                                   :data {:name           "KFC"
                                          :familyFriendly "false"}})))))
