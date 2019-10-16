(ns api.nlg.generator.parser-ng-test
  (:require [api.nlg.generator.parser-ng :as sut]
            [api.test-utils :refer [load-test-document-plan]]
            [clojure.test :refer [deftest is]]))

(deftest simple-plan-parser
  (is (= [[[{:dynamic [{:name  {:cell :product-name :dyn-name "$1"}
                        :attrs {:type :product :source :cell}}]}]]]
         (sut/parse-document-plan (load-test-document-plan "single-subj") nil nil)))

  (is (= [[[{:dynamic [{:name  {:cell :product-name :dyn-name "$1"}
                        :attrs {:source :cell
                                :type   :cell}}]}]]]
         (sut/parse-document-plan (load-test-document-plan "simple-plan") nil nil))))

(deftest amr-plan-parser
  (let [[{quotes-dynamic :dynamic} {agent-dynamic :dynamic} {coagent-dynamic :dynamic}]
        (-> (load-test-document-plan "plain-amr")
            (sut/parse-document-plan  nil nil)
            first first)]
    (is (= [{:name  {:cell :actor :dyn-name "$2"}
             :attrs {:amr true :title "Agent" :source :cell :type :cell}}]
           agent-dynamic))
    (is (= [{:name {:cell :co-actor :dyn-name "$3"}
             :attrs {:amr true :title "co-Agent" :source :cell :type :cell}}]
           coagent-dynamic))
    (let [{:keys [attrs name]} (first quotes-dynamic)]
      (is (= {:source :quotes :type :amr} attrs))
      (is (= "$1" (:dyn-name name)))
      (is (= #{"$2 watches $3" "$2 observe $3" "$2 sees $3"} (set (map :value (:quotes name))))))))
