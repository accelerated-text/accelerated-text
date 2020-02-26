(ns acc-text.nlg.core-test
  (:require [acc-text.nlg.core :as core]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.test-utils :as test-utils]
            [clojure.test :refer [deftest is are]]))

(deftest context-selection
  (let [context {:amr {"nMMpkQyRnXGQPcZT" {:id             "nMMpkQyRnXGQPcZT"
                                           :label          "language-test"
                                           :kind           "Str"
                                           :roles          [{:id "ARG0" :label "item" :type "Str"}]
                                           :semantic-graph (test-utils/load-test-semantic-graph "language-condition")}}}]
    (is (= (core/select-context context {:lang :en})
           '{:amr {"nMMpkQyRnXGQPcZT" {:id             "nMMpkQyRnXGQPcZT"
                                       :kind           "Str"
                                       :label          "language-test"
                                       :roles          [{:id    "ARG0"
                                                         :label "item"
                                                         :type  "Str"}]
                                       :semantic-graph #::sg{:concepts  ({:id   :01
                                                                          :type :document-plan}
                                                                         {:id   :02
                                                                          :type :segment}
                                                                         {:id   :03
                                                                          :type :condition}
                                                                         {:id   :04
                                                                          :type :if-statement}
                                                                         {:id    :08
                                                                          :type  :amr
                                                                          :value "ParadigmsEng.mkN/Str->N"}
                                                                         {:attributes {:name "item"}
                                                                          :id         :09
                                                                          :type       :reference})
                                                             :relations ({:from :01
                                                                          :role :segment
                                                                          :to   :02}
                                                                         {:from :02
                                                                          :role :instance
                                                                          :to   :03}
                                                                         {:from :03
                                                                          :role :statement
                                                                          :to   :04}
                                                                         {:from :04
                                                                          :role :expression
                                                                          :to   :08}
                                                                         {:attributes {:label "Str"
                                                                                       :name  "Str"}
                                                                          :from       :08
                                                                          :role       :ARG0
                                                                          :to         :09})}}}}))
    (is (= (core/select-context context {:lang :ee})
           '{:amr {"nMMpkQyRnXGQPcZT" {:id             "nMMpkQyRnXGQPcZT"
                                       :kind           "Str"
                                       :label          "language-test"
                                       :roles          [{:id    "ARG0"
                                                         :label "item"
                                                         :type  "Str"}]
                                       :semantic-graph #::sg{:concepts  ({:id   :01
                                                                          :type :document-plan}
                                                                         {:id   :02
                                                                          :type :segment}
                                                                         {:id   :03
                                                                          :type :condition}
                                                                         {:id   :10
                                                                          :type :if-statement}
                                                                         {:id    :14
                                                                          :type  :amr
                                                                          :value "ParadigmsEst.mkN/Str->N"}
                                                                         {:attributes {:name "item"}
                                                                          :id         :15
                                                                          :type       :reference})
                                                             :relations ({:from :01
                                                                          :role :segment
                                                                          :to   :02}
                                                                         {:from :02
                                                                          :role :instance
                                                                          :to   :03}
                                                                         {:from :03
                                                                          :role :statement
                                                                          :to   :10}
                                                                         {:from :10
                                                                          :role :expression
                                                                          :to   :14}
                                                                         {:attributes {:label "Str"
                                                                                       :name  "Str"}
                                                                          :from       :14
                                                                          :role       :ARG0
                                                                          :to         :15})}}}}))))

(deftest ^:integration multi-language-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "language-test")
        context (test-utils/load-test-context "language-test")]
    (are [lang result] (= result (map :text (core/generate-text semantic-graph context lang)))
                       :en ["There is a text."]
                       :ee ["On olemas text."]
                       :de ["Es gibt einen text."]
                       :lv ["Ir text."]
                       :ru ["Существует text."])))
