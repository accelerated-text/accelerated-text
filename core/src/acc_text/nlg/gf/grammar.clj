(ns acc-text.nlg.gf.grammar
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(s/def ::label keyword?)

(s/def ::category string?)

(s/def ::literal string?)

(s/def ::value (s/or ::symbol ::literal))

(s/def ::row (s/keys :req [::label ::symbol ::values]))

(defn values->cf [values]
  (string/join " " (map (fn [{literal :acc-text.nlg.gf.syntax/literal
                              symbol  :acc-text.nlg.gf.syntax/category}]
                          (if literal
                            (format "\"%s\"" literal)
                            symbol)) values)))

(defn ->cf [rows]
  (map (fn [{label :acc-text.nlg.gf.syntax/label
             symbol :acc-text.nlg.gf.syntax/category
             values :acc-text.nlg.gf.syntax/values}]
         (format "%s. %s :== %s" label symbol (values->cf values))) rows))

(s/fdef values->cf
  :args (s/coll-of ::value :min-count 1)
  :ret  string?)

(s/fdef ->cf
  :args (s/coll-of ::row :min-count 1)
  :ret  string?)

;; common

(s/def ::module-name  string?)

;; abstract

(s/def ::flags (s/map-of ::label ::category))

(s/def ::categories (s/coll-of ::category :min-count 1 :gen-max 4 :kind set?))

(s/def ::arguments (s/coll-of ::category :min-count 1 :gen-max 4))

(s/def ::return ::category)

(s/def ::function (s/keys :req [::name ::arguments ::return]))

(s/def ::functions (s/coll-of ::function :min-count 1))

(s/def ::abstract-grammar (s/keys :req [::module-name ::flags ::categories ::functions]))

;; concrete

(s/def ::of ::module-name)

(s/def ::lin-types (s/map-of #{"s" "n"} #{"Str" "Number"}))

(s/def ::lin-function (s/keys :req [::name ::arguments ::body]))

(s/def ::concrete-grammar (s/keys :req [::module-name ::of ::lin-types ::lin]))

