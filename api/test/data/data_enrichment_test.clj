(ns data.data-enrichment-test
  (:require  [clojure.test :refer [deftest is]]
             [api.nlg.enrich.data :refer [enrich read-rules]]))

(def enrich-config
  [{:filename-pattern #"accounts.csv"
    :fields
    [{:name-pattern #"Account"
      :transformations
      [{:function :api.nlg.enrich.data.transformations/cleanup
        :args     {:regex #" \(.*?\)" :replacement ""}}]}
     {:name-pattern #".*Period .*"
      :transformations
      [{:function :api.nlg.enrich.data.transformations/number-approximation
        :args     {:scale      1000
                   :language   :en
                   :formatting :numberwords.domain/bites
                   :relation   :numberwords.domain/around}}
       {:function :api.nlg.enrich.data.transformations/add-symbol
        :args     {:symbol " USD" :position :back}}]}
     {:name-pattern #"Increase"
      :transformations
      [{:function :api.nlg.enrich.data.transformations/add-symbol
        :args     {:symbol "$" :position :front :skip #{\- \+}}}]}]}])

(def accounts-data
  [{"Account"            "Gross Sales (ID1220)"
    "CurrentPeriod (Q2)" "90447"
    "PriorPeriod (Q1)"   "82018"
    "Increase"           "8429"}
   {"Account"            "Advertising (ID3011)"
    "CurrentPeriod (Q2)" "1280"
    "PriorPeriod (Q1)"   "1982"
    "Increase"           "-702"}])

(deftest date-enrichment
  (with-redefs [read-rules (fn [] enrich-config)]
    (is (= {"Account"            "Gross Sales",
            "CurrentPeriod (Q2)" "around 90k USD",
            "PriorPeriod (Q1)"   "around 82k USD",
            "Increase"           "$8429"}
           (enrich "accounts.csv" (first accounts-data))))
    (is (= {"Account"            "Advertising"
            "CurrentPeriod (Q2)" "around 1k USD"
            "PriorPeriod (Q1)"   "around 1k USD"
            "Increase"           "-$702"}
           (enrich "accounts.csv" (second accounts-data))))))

