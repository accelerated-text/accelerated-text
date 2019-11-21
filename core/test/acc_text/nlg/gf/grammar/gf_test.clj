(ns acc-text.nlg.gf.grammar.gf-test
  (:require [acc-text.nlg.gf.grammar.gf :as sut]
            [acc-text.nlg.gf.grammar :as grammar]
            [clojure.test :refer [is deftest]]
            [clojure.java.io :as io]))

(defn load-gf [fname]
  (slurp (io/file (format "test/resources/gf/%s" fname))))

(deftest build-simple-abstract-grammar
  (is (= (load-gf "GoodBook.gf") (sut/abstract->gf #::grammar{:module-name "good-book"
                                                              :flags {:startcat "sentence"}
                                                              :categories ["sentence" "data" "modifier"]
                                                              :functions [#::grammar{:function-name "good-title"
                                                                                     :arguments ["modifier" "data"]
                                                                                     :return "sentence"}
                                                                          #::grammar{:function-name "data-title"
                                                                                     :return "data"}
                                                                          #::grammar{:function-name "good-modifier"
                                                                                     :return "modifier"}]}))))

(deftest build-simple-concrete-grammar
  (is (= (load-gf "GoodBookEng.gf") (sut/concrete->gf #::grammar{:module-name "good-book-eng"
                                                                 :of          "good-book"
                                                                 :lin-types   {:Sentence [:s :Str]
                                                                               :Data     [:s :Str]
                                                                               :Modifier [:s :Str]}
                                                                 :lins        [#::grammar{:function-name "good-title"
                                                                                          :syntax [#::grammar{:role :function
                                                                                                              :value "modifier"}
                                                                                                   #::grammar{:role :function
                                                                                                              :value "data"}]}
                                                                               #::grammar{:function-name "data-title"
                                                                                          :syntax [#::grammar{:role :literal
                                                                                                              :value "{{TITLE}}"}]}
                                                                               #::grammar{:function-name "good-modifier"
                                                                                          :syntax [#::grammar{:role :literal
                                                                                                              :value "good"}
                                                                                                   #::grammar{:role :operation
                                                                                                              :value "|"}
                                                                                                   #::grammar{:role :literal
                                                                                                              :value "nice"}]}]}))))
