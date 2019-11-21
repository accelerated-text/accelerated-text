(ns acc-text.nlg.gf.grammar.gf-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.grammar.abstract :as ag]
            [acc-text.nlg.gf.grammar.concrete :as cg]
            [acc-text.nlg.gf.grammar.gf :as sut]
            [acc-text.nlg.test-utils :as utils]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]
            [clojure.string :as string]))

(defn load-gf [fname] (slurp (io/file (format "test/resources/gf/%s" fname))))

(defn split-and-strip [grammar-body] (map string/trim (string/split grammar-body #"\n")))

(defn sg->abstract-grammar-lines [sg-file-name grammar-name]
  (->> sg-file-name 
       (utils/load-test-semantic-graph)
       (ag/build grammar-name)
       (sut/abstract->gf)
       (split-and-strip)))

(defn sg->concrete-grammar-lines [sg-file-name parent-name grammar-name data]
  (-> (cg/build parent-name grammar-name
                (utils/load-test-semantic-graph sg-file-name) data)
      (sut/concrete->gf)
      (split-and-strip)))

(deftest build-simple-abstract-grammar
  (is (= (load-gf "GoodBook.gf")
         (sut/abstract->gf #::grammar{:module-name "GoodBook"
                                      :flags       {:startcat "Sentence"}
                                      :categories  ["Sentence" "Data" "Modifier"]
                                      :functions   [#::grammar{:function-name "GoodTitle"
                                                               :arguments     ["Modifier" "Data"]
                                                               :return        "Sentence"}
                                                    #::grammar{:function-name "DataTitle"
                                                               :return        "Data"}
                                                    #::grammar{:function-name "GoodModifier"
                                                               :return        "Modifier"}]}))))

(deftest build-simple-concrete-grammar
  (is (= (load-gf "GoodBookEng.gf")
         (sut/concrete->gf #::grammar{:module-name "GoodBookEng"
                                      :of          "GoodBook"
                                      :lin-types   {:Sentence [:s :Str]
                                                    :Data     [:s :Str]
                                                    :Modifier [:s :Str]}
                                      :lins        [#::grammar{:function-name "GoodTitle"
                                                               :syntax        [#::grammar{:role  :function
                                                                                          :value "Modifier"}
                                                                               #::grammar{:role  :operation
                                                                                          :value "++"}
                                                                               #::grammar{:role  :function
                                                                                          :value "Data"}]}
                                                    #::grammar{:function-name "DataTitle"
                                                               :syntax        [#::grammar{:role  :literal
                                                                                          :value "{{TITLE}}"}]}
                                                    #::grammar{:function-name "GoodModifier"
                                                               :syntax        [#::grammar{:role  :literal
                                                                                          :value "good"}
                                                                               #::grammar{:role  :operation
                                                                                          :value "|"}
                                                                               #::grammar{:role  :literal
                                                                                          :value "nice"}]}]}))))

(deftest simple-plan->grammar
  (is (= (-> "SimplePlan.gf" load-gf split-and-strip)
         (sg->abstract-grammar-lines "simple-plan" "SimplePlan")))
  (is (= (-> "SimplePlanEng.gf" load-gf split-and-strip)
         (sg->concrete-grammar-lines "simple-plan" "SimplePlan" "SimplePlanEng" {}))))
