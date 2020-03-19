(ns acc-text.nlg.grammar
  (:require [acc-text.nlg.grammar.impl :as impl]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.spec.alpha :as s]))

(s/def ::module string?)

(s/def ::instance string?)

(s/def ::flags (s/map-of keyword? string?))

(s/def ::cat (s/coll-of string?))

(s/def ::fun (s/map-of string? (s/coll-of string?)))

(s/def ::lincat (s/map-of string? string?))

(s/def ::lin (s/map-of string? (s/coll-of string?)))

(s/def ::oper (s/coll-of (s/cat :cat string? :type string? :body string?)))

(s/def ::grammar (s/keys :req [::module ::instance ::flags ::cat ::fun ::lincat ::lin ::oper]))

(defn build-grammar [semantic-graph context]
  (impl/build-grammar semantic-graph context))

(s/fdef build-grammar
        :args (s/cat :semantic-graph ::sg/graph :context map?)
        :ret ::grammar)
