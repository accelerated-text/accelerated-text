(ns acc-text.nlg.gf.generator-test
  (:require [acc-text.nlg.gf.generator :refer [generate]]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build `generate)

(deftest ^:integration quote-cases
  (is (= ["He said: \"GO!\""] (let [semantic-graph (utils/load-test-semantic-graph "quote")
                                    grammar        (grammar/build :grammar :1 semantic-graph {})]
                                (generate grammar)))))

(deftest ^:integration at-location
  (is (get (set (generate (grammar/build :AtLoc :1 (utils/load-test-semantic-graph "location-amr")
                                         {:amr
                                          {:at-location
                                           {:frames
                                            [{:examples ["There is a place in the city center"]
                                              :syntax
                                              [{:type :gf :value "AtLocation"}]}]}}
                                          :dictionary {}})))
           "there is a [VENUE_NAME_ARG] in the [LOCATION_ARG]")))
