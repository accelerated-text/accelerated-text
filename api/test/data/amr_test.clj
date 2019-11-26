(ns data.amr-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]
            [data.entities.amr :as amr]))

(deftest amr-reading
  (is (= {:id                 "author"
          :dictionary-item-id "written"
          :thematic-roles     [{:type "Agent"}
                               {:type "co-Agent"}]
          :frames             [{:examples ["X is the author of Y"]
                                :syntax   [{:pos :NP :role "Agent"}
                                           {:pos :LEX :value "is"}
                                           {:pos :LEX :value "the author of"}
                                           {:pos :NP :role "co-Agent"}]}
                               {:examples ["Y is written by X"]
                                :syntax   [{:pos :NP :role "co-Agent"}
                                           {:pos :LEX :value "is"}
                                           {:pos :VERB}
                                           {:pos :ADP :value "by"}
                                           {:pos :NP :role "Agent"}]}]}
         (amr/read-amr (io/file "test/resources/grammar/library/author.yaml"))))
  (is (= {:id                 "see"
          :dictionary-item-id "see"
          :thematic-roles     [{:type "Agent"}
                               {:type "co-Agent"}]
          :frames             [{:examples ["Harry sees Sally."]
                                :syntax   [{:pos :NP :role "Agent"}
                                           {:pos :VERB}
                                           {:pos :NP :role "co-Agent"}]}]}
         (amr/read-amr (io/file "test/resources/grammar/other/see.yaml"))))
  (is (= {:id                 "provide"
          :dictionary-item-id "provide"
          :thematic-roles     [{:type "Agent"}
                               {:type "co-Agent"}]
          :frames             [{:examples ["Nike provides comfort."]
                                :syntax   [{:pos :NP :role "Agent"}
                                           {:pos :VERB}
                                           {:pos :NP :role "co-Agent"}]}]}
         (amr/read-amr (io/file "test/resources/grammar/other/provide.yaml"))))
  (is (= {:id                 "cut"
          :dictionary-item-id "cut"
          :thematic-roles     [{:type "Agent"}
                               {:type "Patient"}
                               {:type "Instrument"}
                               {:type "Source"}
                               {:type "Result"}]
          :frames             [{:examples ["Carol cut the envelope into pieces with a knife."]
                                :syntax        [{:pos :NP :role "Agent"}
                                                {:pos :VERB}
                                                {:pos :NP :role "Patient"}
                                                {:pos :ADP :value "into"}
                                                {:pos :NP :role "Result"}
                                                {:pos :ADP :value "with"}
                                                {:pos :NP :role "Instrument"}]}]}
         (amr/read-amr (io/file "test/resources/grammar/other/cut.yaml")))))
