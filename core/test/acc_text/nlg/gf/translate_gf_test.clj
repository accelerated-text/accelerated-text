(ns acc-text.nlg.gf.translate-gf-test
  (:require [acc-text.nlg.gf.translate-gf :as sut]
            [acc-text.nlg.gf.grammar :as grammar]
            [clojure.test :refer [is deftest]]
            [clojure.string :as string]))

(def good-title-abstract
  (string/join "\n"
               ["abstract GoodTitle {"
                "flags startcat = Sentence;"
                "cat"
                "Sentence; Data; Modifier;"
                "fun"
                "GoodTitle : Modifier -> Data -> Sentence;"
                "DataTitle : Data;"
                "GoodModifier : Modifier;"
                "}"]))

(deftest build-simple-abstract-grammar
  (is (= good-title-abstract (sut/abstract->gf #::grammar{:module-name "GoodTitle"
                                                          :flags {:startcat "Sentence"}
                                                          :categories ["Sentence" "Data" "Modifier"]
                                                          :functions [#::grammar{:name "GoodTitle"
                                                                                 :arguments ["Modifier" "Data"]
                                                                                 :return "Sentence"}
                                                                      #::grammar{:name "DataTitle"
                                                                                 :return "Data"}
                                                                      #::grammar{:name "GoodModifier"
                                                                                 :return "Modifier"}]}))))
