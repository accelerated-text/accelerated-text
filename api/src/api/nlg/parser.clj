(ns api.nlg.parser
  (:require [api.nlg.parser.impl :as parser]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::concept/id string?)

(s/def ::concept/type #{:root :segment :amr :relationship :data :quote :dictionary-item :unk})

(s/def ::concept (s/keys :req [:id :type]))

(s/def ::concepts (s/coll-of ::concept ::gen-max 5))


(s/def ::role/core
  (s/with-gen keyword?
              #(gen/fmap (fn [idx] (keyword (str "ARG" (Math/abs ^Integer idx)))) (gen/int))))

(s/def ::role/non-core #{:segment :instance :relationship :modifier})

(s/def ::relation/role (s/or :core :core :non-core :non-core))

(s/def ::relation/attributes (s/keys :opt [:name]))

(s/def ::relation
  (s/cat :from ::concept/id
         :to ::concept/id
         :role :role
         :attributes :attributes))

(s/def ::relations (s/coll-of ::relation :gen-max 5))


(s/def ::amr (s/keys :req [::relations ::concepts]))

(defn parse-document-plan [document-plan]
  (parser/parse document-plan))

(s/fdef parse-document-plan
        :args (s/cat :document-plan any?)
        :ret ::amr)
