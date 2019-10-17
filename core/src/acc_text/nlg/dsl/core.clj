(ns acc-text.nlg.dsl.core
  (:require [acc-text.nlg.spec.feature-set :as fs-spec]
            [acc-text.nlg.spec.lexicon :as lex-spec]
            [acc-text.nlg.spec.morphology :as morph-spec]))

(defn fs
  ([predicate]
   #::fs-spec{:index 1
             :features [#::fs-spec{:attribute    "index"
                                  :feature-type :nomvar
                                  :value        predicate}]})
  ([val attr index & features]
   #::fs-spec{:index index
             :val val
             :attr attr
             :features features}))

(defn fs-nomvar
  [attr predicate]
  #::fs-spec{:attribute    attr
            :feature-type :nomvar
            :value        predicate})

(defn fs-feat
  [attr value]
  #::fs-spec{:attribute    attr
            :feature-type :feat
            :value        value})

(defn fs-featvar
  [attr predicate]
  #::fs-spec{:attribute    attr
            :feature-type :featvar
            :value        predicate})

(defn lf
  ([nomvar] #::lex-spec{:nomvar nomvar :predicate "[*DEFAULT*]"})
  ([nomvar predicate & diamonds]
   #::lex-spec{:nomvar nomvar :predicate predicate :diamonds diamonds}))

(defn prop
  ([] #::lex-spec{:name "[*DEFAULT*]"})
  ([name] #::lex-spec{:name name}))

(defn diamond
  [mode {:keys [nomvar prop diamond diamonds]}]
  #::lex-spec{:mode mode
             :prop prop
             :nomvar nomvar
             :diamonds (cons diamond diamonds)})

(defn satop
  [name & children]
  #::lex-spec{:name name :children children})

(defn atomcat
  ([pos predicate]
   {:atomic-cat #::lex-spec{:syntactic-type pos
                           :feature-set  (fs predicate)}})
  ([pos {:keys [index inherits-from]} & features]
   {:atomic-cat #::lex-spec{:syntactic-type pos
                           :feature-set    #::fs-spec{:index index
                                                     :inherits-from inherits-from
                                                     :features features}}}))

(defn family
  ([name pos closed entry]
   #::lex-spec{:pos             pos
              :name            name
              :closed          closed
              :lexical-entries (list entry)})
  ([name pos closed entry & members]
   #::lex-spec{:pos             pos
                          :name            name
                          :closed          closed
                          :lexical-entries (list entry)
                          :members         members}))

(defn entry
  ([name logical-form category]
   #::lex-spec{:name         name
                          :category     category
                          :logical-form logical-form})
  ([name stem logical-form category]
   #::lex-spec{:name         name
                          :predicate    stem
                          :category     category
                          :logical-form logical-form}))

(defn member
  ([stem] #::lex-spec{:stem stem})
  ([stem predicate] #::lex-spec{:stem stem :predicate predicate}))

(defn <B
  ([category1 category2] {:complex-cat [category1 [\\ \*] category2]})
  ([mode category1 category2] {:complex-cat [category1 [\\ mode] category2]}))

(defn >F
  ([category1 category2] {:complex-cat [category1 [\/ \*] category2]})
  ([mode category1 category2] {:complex-cat [category1 [\/ mode] category2]}))

(defn morph-entry
  [word pos {:keys [class stem macros]}]
  #::morph-spec{:word      word
                :pos       pos
                :class     class
                :predicate stem
                :macros    macros})

(defn macro [name fs] #::morph-spec{:name name :fs fs})
