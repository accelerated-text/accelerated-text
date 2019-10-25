(ns api.nlg.parser
  (:require [api.nlg.parser.impl :as parser]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def :acctext.amr/id aphanumeric?)
(s/def :acctext.amr/name string?)

;;FIXME
;; - 'root' is should be renamed to 'document-plan'
;; - 'relationship' is not really a concept node type?
;; - 'unk' should not be allowed, if we want to model it  do it via (s/or :valid ... :bad ...)construction
(s/def :acctext.amr/type #{:root :segment :amr :relationship :data :quote :dictionary-item :unk})

(s/def :acctext.amr/concept (s/keys :req [:acctext.amr/id :acctext.amr/type]))

(s/def :acctext.amr/concepts (s/coll-of :acctext.amr/concept ::gen-max 5))

(s/def :acctext.amr/role
  (s/or :core (s/with-gen keyword?
                #(gen/fmap (fn [idx] (keyword (str "ARG" (Math/abs ^Integer idx)))) (gen/int)))
        :non-core #{:segment :instance :relationship :modifier}))

(s/def :acctext.amr/attributes
  (s/or :has-attrs (s/keys :req [:acctext.amr/name])
        :no-attrs nil?))

(s/def :acctext.amr/relation
  (s/cat :from :acctext.amr/id
         :to :acctext.amr/id
         :role :acctext.amr/role
         :attributes :acctext.amr/attributes))

(s/def :acctext.amr/relations (s/coll-of ::relation :gen-max 5))

(s/def :acctext.amr/graph (s/keys :req [:acctext.amr/relations :acctext.amr/concepts]))

(defn parse-document-plan [document-plan]
  (parser/parse document-plan))

(s/fdef parse-document-plan
        :args (s/cat :document-plan any?)
        :ret :acctext.amr/graph)
