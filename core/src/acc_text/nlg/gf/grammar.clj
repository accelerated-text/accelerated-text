(ns acc-text.nlg.gf.grammar
  (:require [acc-text.nlg.gf.grammar.impl :as impl]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.spec.alpha :as s]))

(s/def ::module keyword?)

(s/def ::instance keyword?)

(s/def ::flags (s/map-of #{:startcat} string? :min-count 1))

(s/def :statement/type #{:operator :function :literal})

(s/def :statement/value string?)

(s/def ::statement (s/keys :req-un [:statement/type :statement/value]))

(s/def :function/name string?)

(s/def :function/args (s/coll-of string?))

(s/def :function/body (s/coll-of ::statement))

(s/def :function/ret #{[:s "Str"]})

(s/def ::function (s/keys :req-un [:function/name :function/args :function/body :function/ret]))

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
