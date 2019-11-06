(ns acc-text.nlg.verbnet.ccg-test
  (:require [acc-text.nlg.grammar :as grammar]
            [acc-text.nlg.spec.morphology :as morph-spec]
            [acc-text.nlg.verbnet.ccg :as ccg]
            [acc-text.nlg.verbnet.grammar-patterns :refer [depth pattern]]
            [acc-text.nlg.verbnet.core :as vnet]
            [clojure.test :refer [deftest is testing]]
            [clojure.tools.logging :as log]))

(def battle (vnet/xml->vclass "test/resources/verbnet/battle.xml"))

(deftest vnet->morph
  (is (= #{#::morph-spec{:word "vie" :pos :VB :predicate "battle" :macros nil :class nil}
           #::morph-spec{:word "war" :pos :VB :predicate "battle" :macros nil :class nil}
           #::morph-spec{:word "wrangle" :pos :VB :predicate "battle" :macros nil :class nil}
           #::morph-spec{:word "{{AGENT}}" :pos :N :predicate "Agent" :macros nil :class nil}
           #::morph-spec{:word "{{CO-AGENT}}" :pos :N :predicate "Co-Agent" :macros nil :class nil}
           #::morph-spec{:word "{{THEME}}" :pos :N :predicate "Theme" :macros nil :class nil}}
         (set (ccg/vclass->morph battle)))))

(deftest depth-test
  (testing "Flat pattern"
    (is (= 0 (depth {:pos :NP}))))
  (testing "One level pattern"
    (is (= 1 (depth (pattern {:pos :VERB} {:pos :NP} {:pos :NP} :start)))))
  (testing "One branch level 3"
    (is (= 3 (depth (pattern {:pos :VERB} (pattern {:pos :VERB}
                                                   (pattern {:pos :VERB} {:pos :NP} {:pos :NP} :end)
                                                   {:pos :NP}
                                                   :end)
                             {:pos :NP} :start))))))

(deftest amr-to-text
  (let [grammars (ccg/vn->grammar battle)]
    (testing "First frame"
      (let [results (set (grammar/generate (first grammars) "{{AGENT}}" "{{CO-AGENT}}" "war"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} war with {{CO-AGENT}}"))))
    (testing "Second frame"
      (let [results (set (grammar/generate (second grammars) "{{AGENT}}" "{{CO-AGENT}}" "war"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} war"))))))

(deftest absorb-amr
  (let [grammars (ccg/vn->grammar (vnet/xml->vclass "test/resources/verbnet/absorb-39.8.xml"))]
    (testing "First frame"
      (let [results (set (grammar/generate (first grammars) "{{GOAL}}" "{{THEME}}" "{{SOURCE}}" "absorb"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{GOAL}} absorb {{THEME}}"))))
    (testing "Second frame"
      ;; NOTE: actual sentence should go like: `{{GOAL}} absorb {{THEME}} from their {{SOURCE}}`.
      ;; AMR itself doesn't provide any indication about that
      (let [results (set (grammar/generate (second grammars) "{{GOAL}}" "{{THEME}}" "{{SOURCE}}" "absorb"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{GOAL}} absorb {{THEME}} from {{SOURCE}}"))))
    ))

(deftest adjust-amr
  (let [grammars (ccg/vn->grammar (vnet/xml->vclass "test/resources/verbnet/adjust-26.9.xml"))]
    (testing "First frame"
      (let [results (set (grammar/generate (first grammars) "{{AGENT}}" "{{PATIENT}}" "{{GOAL}}" "{{SOURCE}}" "adjust"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} adjust {{PATIENT}}"))))
    (testing "Second frame"
      (let [results (set (grammar/generate (second grammars) "{{AGENT}}" "{{PATIENT}}" "{{GOAL}}" "{{SOURCE}}" "adjust"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} adjust {{PATIENT}} to {{GOAL}}"))))
    (testing "Third frame"
      ;; NOTE: restrictor should add `-ing` to GOAL
      (let [results (set (grammar/generate (nth grammars 2) "{{AGENT}}" "{{PATIENT}}" "{{GOAL}}" "{{SOURCE}}" "adjust"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} adjust {{PATIENT}} to {{GOAL}}"))))
    (testing "Fourth frame"
      ;; NOTE: restrictor should add `-ing` to GOAL
      (let [results (set (grammar/generate (nth grammars 3) "{{AGENT}}" "{{PATIENT}}" "{{GOAL}}" "{{SOURCE}}" "adjust"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} adjust to {{GOAL}}"))))
    (testing "Fift frame"
      ;; NOTE: Same as thrird and fourth, but without `-ing`
      (let [results (set (grammar/generate (nth grammars 3) "{{AGENT}}" "{{PATIENT}}" "{{GOAL}}" "{{SOURCE}}" "adjust"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} adjust to {{GOAL}}"))))))

(deftest focus-amr
  (let [grammars (ccg/vn->grammar (vnet/xml->vclass "test/resources/verbnet/focus-87.1.xml"))]
    (testing "First frame"
      (let [results (set (grammar/generate (first grammars) "{{AGENT}}" "{{TOPIC}}" "focus"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} focus on {{TOPIC}}"))))))

(deftest cut-amr
  (let [grammars (ccg/vn->grammar (vnet/xml->vclass "test/resources/verbnet/cut-21.1.xml"))]
    ;; Carol cut the envelope into pieces with a knife.
    (testing "Complicated frame"
      (let [results (set (grammar/generate (first grammars) "{{AGENT}}" "{{PATIENT}}" "{{INSTRUMENT}}" "{{RESULT}}" "cut"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} cut {{PATIENT}} to into {{RESULT}} with {{INSTRUMENT}}")) ;; End result
        ))))

(deftest tape-amr
  (let [grammars (ccg/vn->grammar (-> (vnet/xml->vclass "test/resources/verbnet/tape-22.4.xml")
                                  (assoc :adverbs [{:name "easily"}])))]
    (testing "First frame"
      (let [results (set (grammar/generate (first grammars) "{{AGENT}}" "{{PATIENT}}" "{{CO-PATIENT}}" "{{INSTRUMENT}}" "{{RESULT}}" "pin" "easily"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{PATIENT}} pin easily to on onto {{CO-PATIENT}}"))))))

(deftest admire-amr
  (let [grammars (ccg/vn->grammar (-> (vnet/xml->vclass "test/resources/verbnet/admire-31.2.xml")))]
    (testing "that_comp example"
      (let [results (set (grammar/generate (nth grammars 3) "{{EXPERIENCER}}" "{{STIMULUS}}" "admire"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{EXPERIENCER}} admire that {{STIMULUS}}"))))))

(deftest advise-amr
  ;;Ellen warned Helen how to avoid the crowd.
  (let [grammars (ccg/vn->grammar (-> (vnet/xml->vclass "test/resources/verbnet/advise-37.9.xml")))]
    (testing "wh_inf example"
      (let [results (set (grammar/generate (nth grammars 3) "{{AGENT}}" "{{RECIPIENT}}" "{{TOPIC}}" "alert"))]
        (log/debugf "Results: %s" (pr-str results))
        (is (contains? results "{{AGENT}} alert {{RECIPIENT}} how to {{TOPIC}}"))))))


(def author-amr
  {:id "author"
   :members [{:name "written"}]
   :thematic-roles
   (list {:type "Agent"}
         {:type "co-Agent"})
   :frames
   (list
    {:examples (list "X is the author of Y")
     :syntax
     (list
      {:pos :NP :value "Agent"}
      {:pos :LEX :value "is"}
      {:pos :LEX :value "the author of"}
      {:pos :NP :value "co-Agent"})}
    {:examples (list "Y is written by X")
     :syntax
     (list
      {:pos :NP :value "co-Agent"}
      {:pos :LEX :value "is"}
      {:pos :VERB}
      {:pos :PREP :value "by"}
      {:pos :NP :value "Agent"})})})



(def assume-amr
  ;;adopt-93
  {:id      "assume"
   ;;:dictionary-item-id "assume"
   :members [{:name "assume"}]
   :thematic-roles
   (list {:type "Agent"}
         {:type "Theme"}
         {:type "Time" })
   :frames
   (list
    {:examples (list "the new President will assume office on YYYY-mm-dd")
     :syntax
     (list
      {:pos :NP :value "Agent"}
      {:pos         :VERB
       :restrictors [{:type  :time_future_fixed
                      :value "+"}]}
      {:pos :NP :value "Theme"}
      ;;TODO add specs for restrictors and all of AMR data structures
      {:pos :NP :value "Time" :restrictors [{:type :np_on_inf
                                             :value "+"}]})}

    {:examples (list "Soon, the new President will assume office.")
     :syntax
     (list
      {:pos :NP :value "Time"} ;; TODO: need some restrictor to attach comma after this word
      {:pos :NP :value "Agent"}
      {:pos         :VERB
       :restrictors [{:type  :time_future_fixed
                      :value "+"}]}
      {:pos :NP :value "Theme"})})})

(deftest complex-amr-to-text
  (let [grammars (ccg/vn->grammar author-amr)]
       (testing "First frame"
         (let [results (set (grammar/generate (first grammars) "{{AGENT}}" "{{CO-AGENT}}"))]
           (log/debugf "Results: %s" (pr-str results))
           (is (contains? results "{{AGENT}} is the author of {{CO-AGENT}}"))))
       (testing "Second frame"
         (let [results (set (grammar/generate (second grammars) "{{AGENT}}" "{{CO-AGENT}}" "written"))]
           (log/debugf "Results: %s" (pr-str results))
           (is (contains? results "{{CO-AGENT}} is written by {{AGENT}}"))))))

(deftest adopt-office-amr
  (let [grammars (ccg/vn->grammar assume-amr)
        results (set (flatten (map #(grammar/generate % "{{AGENT}}" "{{THEME}}" "{{TIME}}" "assume") grammars)))]
    (log/debugf "Results: %s" (pr-str results))
    #_(is (contains? results "{{TIME}}, {{AGENT}} will assume {{THEME}}"))
    (is (contains? results "{{AGENT}} will assume {{THEME}} on {{TIME}}"))))
