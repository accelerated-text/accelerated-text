(ns acc-text.nlg.spec.amr-graph
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as string]))

(s/def :acctext.amr/id keyword?)
(s/def :acctext.amr/name (s/and string? #(not (string/blank? %))))

(s/def :acctext.amr/type (s/or :valid #{:document-plan :segment :data :quote :dictionary-item}
                               :invalid #{:amr :unknown}))

(s/def :acctext.amr/concept (s/keys :req [:acctext.amr/id :acctext.amr/type]))

(s/def :acctext.amr/concepts (s/coll-of :acctext.amr/concept :min-count 1))

(s/def :acctext.amr/role
  (s/or :core (s/with-gen keyword? #(gen/fmap (fn [idx] (keyword (str "ARG" (Math/abs ^Integer idx)))) (gen/int)))
        :non-core #{:segment :instance :modifier}
        :invalid #{:unknown}))

(s/def :acctext.amr/attributes
  (s/or :has-attrs (s/keys :req [:acctext.amr/name])
        :no-attrs nil?))

(s/def :acctext.amr/relation
  (s/cat :from :acctext.amr/id
         :to :acctext.amr/id
         :role :acctext.amr/role
         :attributes :acctext.amr/attributes))

(s/def :acctext.amr/relations (s/coll-of :acctext.amr/relation))

(s/def :acctext.amr/graph (s/keys :req [:acctext.amr/relations :acctext.amr/concepts]))
