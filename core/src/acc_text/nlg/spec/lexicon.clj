(ns acc-text.nlg.spec.lexicon
  (:require [acc-text.nlg.spec.common :as common]
            [acc-text.nlg.spec.feature-set :as fs-spec]
            [clojure.spec.alpha :as s]))

(s/def ::closed boolean?)

(s/def ::predicate common/simple-string?)

(s/def ::name common/simple-string?)

(s/def ::stem common/simple-string?)

(s/def ::combining-type
  (s/cat :slash #{\/ \\} :dot #{\* \. \>}))

(s/def ::pos
  #{:S :NNP :NP :CC :VB :PRP})

(s/def ::syntactic-type
  #{:s :n :x})

(s/def ::feature-set ::fs-spec/feature-set)

(s/def ::atomic-cat
  (s/keys :req [::syntactic-type] :opt [::feature-set]))

(s/def ::complex-cat
  (s/tuple (s/or :atomic-cat ::atomic-cat
                 :complex-cat ::complex-cat)
           ::combining-type
           (s/or :atomic-cat ::atomic-cat
                 :complex-cat ::complex-cat)))

(s/def ::category
  (s/or :complex-cat ::complex-cat
        :atomic-cat  ::atomic-cat))

;;<satop nomvar="E">
;;  <prop name="[*DEFAULT*]"/>
;;  <diamond mode="Subject">
;;    <nomvar name="X"/>
;;  </diamond>
;;  <diamond mode="Object">
;;    <nomvar name="Y"/>
;;  </diamond>
;;</satop>


(s/def ::mode common/simple-string?)
(s/def ::nomvar common/simple-string?)

(s/def ::prop (s/cat :name ::name))

(s/def ::diamond
  (s/keys :req [::mode] :opt [::nomvar ::diamonds ::prop]))

(s/def ::diamonds
  (s/coll-of ::diamond))

(s/def ::logical-form
  (s/keys :req [::nomvar] :opt [::diamonds ::predicate]))

;;Template takes a predicate from ::lexeme and maps it on to a full lexical item.
;;For example, there is a single template that can map the ::lexeme example above
;;to the final lexical entry `Boston @ NP : bos`
(s/def ::lexical-entry
  (s/keys :req [::category]
          ;;`predicate` is not really optional but OpenCCG uses
          ;;[*DEFAULT*] value for not specified predicates
          :opt [::name ::predicate ::logical-form]))

(s/def ::lexical-entries (s/coll-of ::lexical-entry :min-count 1))

(s/def ::family (s/keys :req [::pos ::lexical-entries]
                        :opt [::closed ::members]))

(s/def ::lexicon (s/coll-of ::family :min-count 1))

(s/def ::member (s/keys :req [::stem]
                        :opt [::predicate]))

(s/def ::members (s/coll-of ::members :min-count 0))
