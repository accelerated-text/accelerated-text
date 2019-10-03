(ns acc-text.nlg.verbnet.core-test
  (:require [acc-text.nlg.verbnet.core :as vn]
            [clojure.test :refer [deftest is]]))

(deftest test-verbnet-xml-parsing
  (let [{:keys [members thematic-roles frames]}
        (vn/xml->vclass "test/resources/verbnet/battle.xml")
        {:keys [description examples syntax semantics]} (first frames)
        syntax-2 (-> frames second :syntax)]

    (is (= 2 (count frames)))

    (is (= [{:pos :NP
             :value "Agent"
             :restrictors [{:value "+", :type "plural"}]}
            {:pos :VERB}]
           syntax-2))

    (is (= [{:name "vie" :wn "vie%2:33:00" :grouping ""}
            {:name "war" :wn "war%2:33:00" :grouping ""}
            {:name "wrangle" :wn "wrangle%2:32:00" :grouping ""}]
           members))

    (is (= [{:type "Agent"
             :selection-restrictions
             [{:logic       "or"
               :restrictors [{:value "+" :type "animate"}
                             {:value "+" :type "organization"}]}]}
            {:type "Co-Agent"}
            {:type "Theme"}]
           thematic-roles))

    (is (= {:descriptionNumber "8.1"         :primary "NP V PP.co-agent"
            :secondary         "PP; with-PP" :xtag    "0.2"}
           description))

    (is (= ["Sparta battled with Athens."] examples))

    (is (= [{:pos :NP :value "Agent"}
            {:pos :VERB}
            {:pos :PREP :value "with"}
            {:pos :NP :value "Co-Agent"}]
           syntax))

    (is (= [{:value     "social_interaction"
             :arguments [{:type "Event" :value "during(E)"}
                         {:type "ThemRole" :value "Agent"}
                         {:type "ThemRole" :value "Co-Agent"}]}
            {:value     "conflict"
             :arguments [{:type "Event" :value "during(E)"}
                         {:type "ThemRole" :value "Agent"}
                         {:type "ThemRole" :value "Co-Agent"}]}
            {:value     "contact"
             :bool      "?"
             :arguments [{:type "Event" :value "during(E)"}
                         {:type "ThemRole" :value "Agent"}
                         {:type "ThemRole" :value "Co-Agent"}]}
            {:value     "manner"
             :arguments [{:type "Constant" :value "hostile"}
                         {:type "ThemRole" :value "Agent"}
                         {:type "ThemRole" :value "Co-Agent"}]}]
           semantics))))
