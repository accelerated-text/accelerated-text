(ns acc-text.nlg.paradigms-test
  (:require [acc-text.nlg.paradigms.lang :refer [resolve-dict-item]]
            [acc-text.nlg.paradigms.utils :refer [find-root gen-id]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.core :refer [generate-text]]
            [acc-text.nlg.dictionary.item :as dict-item]
            [clojure.test :refer [deftest is are]]))

(deftest ^:integration dictionary-item-generation
  (are [result dict-item]
    (let [dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    (find-root dict-item-graph)
                                                             :role  :instance
                                                             :index 0}]))]
      (= result (map :text (generate-text semantic-graph {} "Eng"))))

    ["Cat."] #::dict-item{:key        "cat_N"
                          :category   "N"
                          :language   "Eng"
                          :forms      ["cat"]
                          :attributes {"Gender" "nonhuman"}}))
