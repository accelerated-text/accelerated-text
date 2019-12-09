(ns data.document-plan-test
  (:require [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures testing]]
            [data.entities.document-plan :as document-plan]))

(use-fixtures :each fixtures/clean-db)

(deftest ^:integration io-document-plan
  (testing "title document plan storing"
    (let [dp {:uid          "01"
              :name         "title-only"
              :documentPlan {:segments
                                    [{:children
                                             [{:name  "title"
                                               :type  "Cell"
                                               :srcId "isiyTw"}]
                                      :type  "Segment"
                                      :srcId "%!Y"}]
                             :type  "Document-plan"
                             :srcId "G=Rh"}}
          {id :id :as resp} (document-plan/add-document-plan dp)]
      (is (= #{:updatedAt :uid :name :createdAt :id :documentPlan :updateCount}
             (set (keys resp))))
      (is (string? id))

      (let [{doc-plan :documentPlan} (document-plan/get-document-plan id)]
        (is (= {:segments
                       [{:children [{:name "title" :type "Cell" :srcId "isiyTw"}]
                         :type     "Segment"
                         :srcId    "%!Y"}]
                :type  "Document-plan"
                :srcId "G=Rh"}
               doc-plan))))))

(deftest ^:integration authorship
  (testing "authorship document plan"
    (let [dp {:uid          "02"
              :name         "authorship"
              :documentPlan {:segments
                                    [{:children
                                             [{:type           "AMR" :srcId "W}lSg%-S(rQ*nmVp3fFV"
                                               :conceptId      "author"
                                               :dictionaryItem {:name "author" :type "Dictionary-item" :srcId "jirq-V{WgCd+u[sdVWpd" :itemId "VB-author"}
                                               :roles          [{:name     "agent"
                                                                 :children [{:name  "authors"
                                                                             :type  "Cell"
                                                                             :srcId "uakxT`=!W@8/xV#^orHk"}]}
                                                                {:name     "co-agent"
                                                                 :children [{:name "title" :type "Cell" :srcId "X_CwogT+.Z)N@;Mhz.j/"}]}
                                                                {:name "theme" :children [nil]}]}]
                                      :type  "Segment"
                                      :srcId "}0Ci`hF%i?izegwAT[@J"}]
                             :type  "Document-plan"
                             :srcId "eoPNHZ1PSV{MJBwehL^Z"}}
          {id :id :as resp} (document-plan/add-document-plan dp)]
      (is (= #{:updatedAt :uid :name :createdAt :id :documentPlan :updateCount}
             (set (keys resp))))
      (is (string? id))

      (let [{doc-plan :documentPlan} (document-plan/get-document-plan id)]
        (is (= {:segments [{:children [{:conceptId      "author"
                                        :dictionaryItem {:itemId "VB-author"
                                                         :name   "author"
                                                         :srcId  "jirq-V{WgCd+u[sdVWpd"
                                                         :type   "Dictionary-item"}
                                        :roles          [{:children [{:name  "authors"
                                                                      :srcId "uakxT`=!W@8/xV#^orHk"
                                                                      :type  "Cell"}]
                                                          :name     "agent"}
                                                         {:children [{:name  "title"
                                                                      :srcId "X_CwogT+.Z)N@;Mhz.j/"
                                                                      :type  "Cell"}]
                                                          :name     "co-agent"}
                                                         {:children [nil]
                                                          :name     "theme"}]
                                        :srcId          "W}lSg%-S(rQ*nmVp3fFV"
                                        :type           "AMR"}]
                            :srcId    "}0Ci`hF%i?izegwAT[@J"
                            :type     "Segment"}]
                :srcId    "eoPNHZ1PSV{MJBwehL^Z"
                :type     "Document-plan"}
               doc-plan))))))

(deftest ^:integration adjective
  (let [dp {:uid          "03"
            :name         "adjective-phrase"
            :documentPlan {:segments [{:children [{:child  {:name "title" :type "Cell" :srcId "k1*3(#7IWxHal=%)AdyQ"}
                                                   :name   "good"
                                                   :type   "Dictionary-item-modifier"
                                                   :srcId  "hy-Io!DlnURxCO!v3`^["
                                                   :itemId "NN-good"}]
                                       :type     "Segment"
                                       :srcId    "ujW*X(khAvxZNh!jF8c8"}]
                           :type     "Document-plan" :srcId "xlp%{tSm4kq9Y?|jz(7e"}}
        {id :id :as resp} (document-plan/add-document-plan dp)]
    (is (= #{:updatedAt :uid :name :createdAt :id :documentPlan :updateCount}
           (set (keys resp))))
    (is (string? id))

    (let [{doc-plan :documentPlan} (document-plan/get-document-plan id)]
      (is (= {:segments [{:children [{:child  {:name  "title"
                                               :srcId "k1*3(#7IWxHal=%)AdyQ"
                                               :type  "Cell"}
                                      :itemId "NN-good"
                                      :name   "good"
                                      :srcId  "hy-Io!DlnURxCO!v3`^["
                                      :type   "Dictionary-item-modifier"}]
                          :srcId    "ujW*X(khAvxZNh!jF8c8"
                          :type     "Segment"}]
              :srcId    "xlp%{tSm4kq9Y?|jz(7e"
              :type     "Document-plan"}
             doc-plan)))))
