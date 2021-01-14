(ns acc-text.nlg.semantic-graph.relation
  (:require [clojure.spec.alpha :as s]))

(s/def ::from string?)

(s/def ::to string?)

(s/def ::role
  (s/or :core #{:arg}
        :non-core #{:segment :instance :modifier :child :item :statement :predicate
                    :then-expression :comparable :input :definition :pointer}))

(s/def ::index int?)

(s/def ::category string?)

(s/def ::name string?)
