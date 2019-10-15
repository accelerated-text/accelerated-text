(ns acc-text.nlg.grammar
  (:require [acc-text.nlg.combinator :as combinator]
            [acc-text.nlg.realizer :as realizer]
            [acc-text.nlg.utils :as utils]
            [clojure.tools.logging :as log])
  (:import [org.jdom Element]
           [opennlp.ccg.grammar Grammar ForwardApplication BackwardApplication] 
           [opennlp.ccg.builders LexiconBuilder RulesBuilder TypesBuilder GrammarBuilder]
           [opennlp.ccg.lexicon Family DataItem EntriesItem MorphItem MacroItem]))

(defn element->EntriesItem [family el] (new EntriesItem el family))

(defn element->MorphItem [el] (new MorphItem el))

(defn element->MacroItem [el] (new MacroItem el))

(defn build-lexicon [{:keys [families morph macros]}]
  (fn [grammar]
    (let [builder (LexiconBuilder/builder)]
      (log/debug "Linking initial Lexicon")
      (.withLexicon grammar (.ref builder)) ;; Link Lexicon with Grammar
      (doseq [f families] (.addFamily builder f))
      (doseq [m morph] (.addMorph builder (element->MorphItem m)))
      (doseq [m macros] (.addMacro builder (element->MacroItem m)))
      (log/debugf "Lexicon with %d families, %d morph, %d macros"
                  (count families) (count morph) (count macros))
      (.build builder))))

(defn build-types [types]
  (let [builder (TypesBuilder/builder)]
    (doseq [{:keys [name parents]} types]
      (if-not (nil? parents)
        (.addType builder name parents)
        (.addType builder name)))
    (.build builder)))

(defn build-rules [rules]
  (let [builder (RulesBuilder/builder)]
    (doseq [r rules] (.addRule builder r))
    (.build builder)))

(defn build-default-rules []
  (build-rules [(ForwardApplication.) (BackwardApplication.)]))

(defn build-grammar [{:keys [rules types]}]
  (log/debugf "Adding types: %s" (pr-str (map #(.getName %) (.getIndexMap types))))
  (let [builder (-> (GrammarBuilder/builder)
                    (.withRules rules)
                    (.withTypes types))]
    (log/debugf "Global grammar initialized? %s" (.isGlobalGrammarInit builder))
    (log/info "Have initial grammar")
    (fn [lexicon]
      (lexicon builder) ;; Link Lexicon with Grammar
      (.build builder))))

(def max-depth
  (or (utils/str->int (System/getenv "MAX_DEPTH")) 7))

(defn generate
  "Generates multiple of valid sentences from given array of tokens"
  [^Grammar grammar & tokens]
  (combinator/reset-indices)
  (let [signs (flatten (map (partial utils/str->sign grammar) tokens))
        combinations (combinator/combinate grammar signs max-depth)
        ;;If we have one token in then we are likely dealing with partial sentences,
        ;;generated for a simple statement plans. Like {{TITLE}}
        ;;FIXME
        sentences (if (= 1 (count tokens))
                    (filter utils/partial-sentence? combinations)
                    (filter utils/sentence? combinations))
        ;; sentences combinations
        results (flatten (map (partial realizer/realize-sign grammar) sentences))
        strs (map utils/sign->str results)]
    (log/debugf "Combination count: %d. Sentences count: %d Results: %d"
                (count combinations) (count sentences) (count results))
    (map utils/clean-sentence strs)))
