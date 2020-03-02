(ns acc-text.nlg.semantic-graph
  (:require [clojure.spec.alpha :as s]))

(s/def :acc-text.nlg.semantic-graph.concept/id keyword?)

(s/def :acc-text.nlg.semantic-graph.concept/value string?)

(s/def :acc-text.nlg.semantic-graph.concept.attributes/kind string?)

(s/def :acc-text.nlg.semantic-graph.concept/attributes
  (s/keys :opt-un [:acc-text.nlg.semantic-graph.concept.attributes/kind]))

(s/def :acc-text.nlg.semantic-graph.relation/from keyword?)

(s/def :acc-text.nlg.semantic-graph.relation/to keyword?)

(s/def :acc-text.nlg.semantic-graph.relation.attributes/name string?)
(s/def :acc-text.nlg.semantic-graph.relation.attributes/label string?)

(s/def :acc-text.nlg.semantic-graph.relation/attributes
  (s/keys :opt-un [:acc-text.nlg.semantic-graph.relation.attributes/name
                   :acc-text.nlg.semantic-graph.relation.attributes/label]))

(s/def :acc-text.nlg.semantic-graph.concept/type
  #{:document-plan :segment :data :quote :dictionary-item :amr :shuffle :sequence :condition :if-statement
    :default-statement :comparator :boolean :variable :reference :modifier :constant})

(s/def ::concepts
  (s/coll-of
    (s/keys :req-un [:acc-text.nlg.semantic-graph.concept/id
                     :acc-text.nlg.semantic-graph.concept/type]
            :opt-un [:acc-text.nlg.semantic-graph.concept/value
                     :acc-text.nlg.semantic-graph.concept/attributes])))

(s/def :acc-text.nlg.semantic-graph.relation/role
  (s/or :core (s/and keyword? #(or (= :function %) (re-matches #"^ARG\d+$" (name %))))
        :non-core #{:segment :instance :modifier :child :item :statement :predicate :comparable :expression :entity
                    :input :definition :pointer}))

(s/def ::relations
  (s/coll-of
    (s/keys :req-un [:acc-text.nlg.semantic-graph.relation/from
                     :acc-text.nlg.semantic-graph.relation/to
                     :acc-text.nlg.semantic-graph.relation/role]
            :opt-un [:acc-text.nlg.semantic-graph.relation/attributes])))

(s/def ::graph
  (s/keys :req [::relations ::concepts]))
