(ns data.amr-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]
            [data.entities.amr :as amr]))

(deftest amr-reading
  (is (= {:id     "author"
          :roles  [{:type "lexicon"}
                   {:type "Agent"}
                   {:type "co-Agent"}]
          :frames [{:examples ["X is the author of Y"]
                    :syntax   [{:pos :NP :role "Agent"}
                               {:pos :AUX :value "is"}
                               {:pos :LEX :value "the author of"}
                               {:pos :NP :role "co-Agent"}]}
                   {:examples ["Y is written by X"]
                    :syntax   [{:pos :NP :role "co-Agent"}
                               {:pos :AUX :value "is"}
                               {:pos :VERB :role "lexicon"}
                               {:pos :ADP :value "by"}
                               {:pos :NP :role "Agent"}]}]}
         (amr/read-amr "author" (slurp (io/file "test/resources/grammar/library/author.yaml")))))
  (is (= {:id     "see"
          :roles  [{:type "lexicon"}
                   {:type "Agent"}
                   {:type "co-Agent"}]
          :frames [{:examples ["Harry sees Sally."]
                    :syntax   [{:pos :NP :role "Agent"}
                               {:pos :VERB :role "lexicon"}
                               {:pos :NP :role "co-Agent"}]}]}
         (amr/read-amr "see" (slurp (io/file "test/resources/grammar/other/see.yaml")))))
  (is (= {:id     "cut"
          :roles  [{:type "lexicon"}
                   {:type "Agent"}
                   {:type "Patient"}
                   {:type "Instrument"}
                   {:type "Source"}
                   {:type "Result"}]
          :frames [{:examples ["Carol cut the envelope into pieces with a knife."]
                    :syntax   [{:pos :NP :role "Agent"}
                               {:pos :VERB :role "lexicon"}
                               {:pos :NP :role "Patient"}
                               {:pos :ADP :value "into"}
                               {:pos :NP :role "Result"}
                               {:pos :ADP :value "with"}
         (amr/read-amr "cut" (slurp (io/file "test/resources/grammar/other/cut.yaml"))))))
