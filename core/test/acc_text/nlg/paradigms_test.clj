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
      (is (= result (map :text (generate-text semantic-graph {} "Eng")))))

    ["Cat."] #::dict-item{:key        "cat_N"
                          :category   "N"
                          :language   "Eng"
                          :forms      ["cat"]
                          :attributes {"Gender" "nonhuman"}}

    ["Vilnius."] #::dict-item{:key        "Vilnius_PN"
                              :category   "PN"
                              :language   "Eng"
                              :forms      ["Vilnius"]
                              :attributes {"Gender" "nonhuman"}}

    ["Mother."] #::dict-item{:key        "mother_N2"
                             :category   "N2"
                             :language   "Eng"
                             :forms      ["mother"]
                             :attributes {"Post" "of"}}

    ["Son."] #::dict-item{:key        "son_N3"
                          :category   "N3"
                          :language   "Eng"
                          :forms      ["son"]
                          :attributes {"Prep" "from"
                                       "Post" "to"}}

    ["At."] #::dict-item{:key      "at_Prep"
                         :category "Prep"
                         :language "Eng"
                         :forms    ["at"]}

    ["About."] #::dict-item{:key      "about_Post"
                            :category "Post"
                            :language "Eng"
                            :forms    ["about"]}

    ["I."] #::dict-item{:key        "I_Pron"
                        :category   "Pron"
                        :language   "Eng"
                        :forms      ["I" "me" "my" "mine"]
                        :attributes {"Number" "singular"
                                     "Person" "P1"
                                     "Gender" "human"}}

    ["Although."] #::dict-item{:key      "although_Subj"
                               :category "Subj"
                               :language "Eng"
                               :forms    ["although"]}

    ["Alas."] #::dict-item{:key      "alas_Interj"
                           :category "Interj"
                           :language "Eng"
                           :forms    ["alas"]}

    ["To feel."] #::dict-item{:key      "feel_V"
                              :category "V"
                              :language "Eng"
                              :forms    ["feel" "felt" "felt"]}

    ["To run from itself."] #::dict-item{:key        "run_V2"
                                         :category   "V2"
                                         :language   "Eng"
                                         :forms      ["run" "ran" "run"]
                                         :attributes {"Post" "from"}}))
