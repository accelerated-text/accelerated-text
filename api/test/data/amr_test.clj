(ns data.amr-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]
            [data.entities.amr :as amr]
            [data.utils :as utils]))

(deftest amr-parsing
  (is (= {:id                 "author"
          :dictionary-item-id "written"
          :thematic-roles     [{:type "Agent"}
                               {:type "co-Agent"}]
          :frames             [{:examples ["X is the author of Y"]
                                :syntax   [{:pos :NP :value "Agent"}
                                           {:pos :LEX :value "is"}
                                           {:pos :LEX :value "the author of"}
                                           {:pos :NP :value "co-Agent"}]}
                               {:examples ["Y is written by X"]
                                :syntax   [{:pos :NP :value "co-Agent"}
                                           {:pos :LEX :value "is"}
                                           {:pos :VERB}
                                           {:pos :PREP :value "by"}
                                           {:pos :NP :value "Agent"}]}]}
         (amr/parse-amr (utils/read-yaml (io/file "test/resources/amr/author.yaml")))))
  (is (= {:id                 "see"
          :dictionary-item-id "see"
          :thematic-roles     [{:type "Agent"}
                               {:type "co-Agent"}]
          :frames             [{:examples ["Harry sees Sally."]
                                :syntax   [{:pos :NP :value "Agent"}
                                           {:pos :VERB}
                                           {:pos :NP :value "co-Agent"}]}]}
         (amr/parse-amr (utils/read-yaml (io/file "test/resources/amr/see.yaml")))))
  (is (= {:id                 "provide"
          :dictionary-item-id "provide"
          :thematic-roles     [{:type "Agent"}
                               {:type "co-Agent"}]
          :frames             [{:examples ["Nike provides comfort."]
                                :syntax   [{:pos :NP :value "Agent"}
                                           {:pos :VERB}
                                           {:pos :NP :value "co-Agent"}]}]}
         (amr/parse-amr (utils/read-yaml (io/file "test/resources/amr/provide.yaml"))))))
