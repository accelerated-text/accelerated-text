(ns amr-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::core-role
  (s/with-gen keyword?
              #(gen/fmap (fn [idx] (keyword (str "ARG" (Math/abs ^Integer idx)))) (gen/int))))

(s/def ::non-core-role #{:part :location})

(s/def ::name (s/or :core ::core-role
                    :non-core ::non-core-role))

(s/def ::edge-attributes (s/keys :req [::name]))

(s/def ::id string?)

(s/def ::type #{:x :y :z})

(s/def ::edge
  (s/cat :from ::id
         :to ::id
         :attributes ::edge-attributes))

(s/def ::relations (s/coll-of ::edge :gen-max 5))

(s/def ::concept (s/keys :req [::id ::type]))

(s/def ::concepts (s/coll-of ::concept ::gen-max 5))

(s/def ::amr (s/keys :req [::relations ::concepts]))
