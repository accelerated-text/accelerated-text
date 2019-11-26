(ns acc-text.nlg.semantic-graph
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as string]))

(s/def ::id keyword?)

(s/def ::name (s/and string? #(not (string/blank? %))))

(s/def ::type (s/or :valid #{:document-plan :segment :data :quote :dictionary-item}
                    :invalid #{:amr :unknown}))

(s/def ::concept (s/keys :req [::id ::type]))

(s/def ::concepts (s/coll-of ::concept :min-count 1))

(s/def ::role
  (s/or :core (s/with-gen keyword? #(gen/fmap (fn [idx] (keyword (str "ARG" (Math/abs ^Integer idx)))) (gen/int)))
        :non-core #{:segment :instance :modifier}
        :invalid #{:unknown}))

(s/def ::from keyword?)
(s/def ::to keyword?)

(s/def ::attributes
  (s/or :has-attrs (s/keys :req [::name])
        :no-attrs nil?))

(s/def ::relation
  (s/keys :req [::from ::to ::role]
          :opt [::attributes]))

(s/def ::relations (s/coll-of ::relation))

(s/def ::graph (s/keys :req [::relations ::concepts]))

(s/def ::document-plan-id string?)

(s/def ::dictionary map?)

(s/def ::reader-profile keyword?)

(s/def ::reader-profiles (s/coll-of ::reader-profile))

(s/def ::amr map?)

(s/def ::context (s/keys :req [::document-plan-id ::dictionary ::reader-profile ::amr]))

(s/def ::instance (s/keys :req [::id ::context ::graph]))
