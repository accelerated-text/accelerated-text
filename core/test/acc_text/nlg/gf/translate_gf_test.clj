(ns acc-text.nlg.gf.translate-gf-test
  (:require [acc-text.nlg.gf.translate-gf :as sut]
            [acc-text.nlg.gf.grammar :as grammar]
            [clojure.test :refer [is deftest]]
            [clojure.string :as string]
            [clojure.java.io :as io]))


(defn load-gf [fname]
  (slurp (io/file (format "test/resources/gf/%s" fname))))

(deftest build-simple-abstract-grammar
  (is (= (load-gf "GoodBook.gf") (sut/abstract->gf #::grammar{:module-name "GoodTitle"
                                                              :flags {:startcat "Sentence"}
                                                              :categories ["Sentence" "Data" "Modifier"]
                                                              :functions [#::grammar{:name "GoodTitle"
                                                                                     :arguments ["Modifier" "Data"]
                                                                                     :return "Sentence"}
                                                                          #::grammar{:name "DataTitle"
                                                                                     :return "Data"}
                                                                          #::grammar{:name "GoodModifier"
                                                                                     :return "Modifier"}]}))))

(deftest build-simple-concrete-grammar
  (is (= (load-gf "GoodBookEng.gf") (sut/concrete->gf #::grammar{:module-name "GoodTitle01"
                                                                 :of          "GoodTitle"}))))
