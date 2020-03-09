(ns acc-text.nlg.gf.grammar
  (:require [acc-text.nlg.gf.grammar.impl :as impl]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.spec.alpha :as s]))

(s/def ::module string?)

(s/def ::instance string?)

(s/def ::flags (s/map-of #{:startcat} string? :min-count 1))

(s/def :acc-text.nlg.gf.grammar.expression/kind #{:variable :function :literal :operation})

(s/def :acc-text.nlg.gf.grammar.expression/value string?)

(s/def :acc-text.nlg.gf.grammar.expression/params (s/coll-of
                                                    (s/keys :req-un [:acc-text.nlg.gf.grammar.expression/kind
                                                                     :acc-text.nlg.gf.grammar.expression/value])))

(s/def :acc-text.nlg.gf.grammar.expression/selectors (s/map-of #{:tense :number} keyword?))

(s/def ::expression (s/keys :req-un [:acc-text.nlg.gf.grammar.expression/kind
                                     :acc-text.nlg.gf.grammar.expression/value]
                            :opt-un [:acc-text.nlg.gf.grammar.expression/selectors
                                     :acc-text.nlg.gf.grammar.expression/params]))

(s/def :acc-text.nlg.gf.grammar.function/name string?)

(s/def :acc-text.nlg.gf.grammar.function/type keyword?)

(s/def :acc-text.nlg.gf.grammar.function/params (s/coll-of string?))

(s/def :acc-text.nlg.gf.grammar.function/body (s/* (s/or :expression ::expression
                                                         :nested-expression (s/coll-of ::expression))))

(s/def :acc-text.nlg.gf.grammar.function/ret #{[:s "Str"]})

(s/def ::function (s/keys :req-un [:acc-text.nlg.gf.grammar.function/name
                                   :acc-text.nlg.gf.grammar.function/type
                                   :acc-text.nlg.gf.grammar.function/params
                                   :acc-text.nlg.gf.grammar.function/body
                                   :acc-text.nlg.gf.grammar.function/ret]))

(s/def ::functions (s/coll-of ::function))

(s/def :acc-text.nlg.gf.grammar.variable/name string?)

(s/def :acc-text.nlg.gf.grammar.variable/value (s/coll-of string?))

(s/def :acc-text.nlg.gf.grammar.variable/item map?)

(s/def :acc-text.nlg.gf.grammar.variable/type string?)

(s/def ::variable (s/keys :req-un [:acc-text.nlg.gf.grammar.variable/name
                                   :acc-text.nlg.gf.grammar.variable/value
                                   :acc-text.nlg.gf.grammar.variable/type]))

(s/def ::variables (s/coll-of ::variable))

(s/def ::grammar (s/keys :req [::module ::instance ::flags ::variables ::functions]))

(defn build [module instance semantic-graph context]
  (impl/build-grammar module instance semantic-graph context))

(s/fdef build
        :args (s/cat :module ::module
                     :instance ::instance
                     :semantic-graph ::sg/graph
                     :context map?)
        :ret ::grammar)
