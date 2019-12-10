(ns acc-text.nlg.gf.grammar
  (:require [acc-text.nlg.gf.grammar.impl :as impl]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.spec.alpha :as s]))

(s/def ::module keyword?)

(s/def ::instance keyword?)

(s/def ::flags (s/map-of #{:startcat} string? :min-count 1))

(s/def :acc-text.nlg.grammar.expression/type #{:function :literal})

(s/def :acc-text.nlg.grammar.expression/value string?)

(s/def :acc-text.nlg.grammar.expression/selectors (s/map-of #{:tense :number} keyword?))

(s/def ::expression (s/keys :req-un [:acc-text.nlg.grammar.expression/type
                                     :acc-text.nlg.grammar.expression/value]
                            :opt-un [:acc-text.nlg.grammar.expression/selectors]))

(s/def :acc-text.nlg.grammar.function/name string?)

(s/def :acc-text.nlg.grammar.function/params (s/coll-of string?))

(s/def :acc-text.nlg.grammar.function/body (s/* (s/or :expression ::expression
                                                      :nested-expression (s/coll-of ::expression))))

(s/def :acc-text.nlg.grammar.function/ret #{[:s "Str"]})

(s/def ::function (s/keys :req-un [:acc-text.nlg.grammar.function/name
                                   :acc-text.nlg.grammar.function/params
                                   :acc-text.nlg.grammar.function/body
                                   :acc-text.nlg.grammar.function/ret]))

(s/def ::syntax (s/coll-of ::function))

(s/def ::grammar (s/keys :req [::module ::instance ::flags ::syntax]))

(defn build [module instance semantic-graph context]
  (impl/build-grammar module instance semantic-graph context))

(s/fdef build
        :args (s/cat :module ::module
                     :instance ::instance
                     :semantic-graph ::sg/graph
                     :context map?)
        :ret ::grammar)
