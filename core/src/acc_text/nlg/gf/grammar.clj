(ns acc-text.nlg.gf.grammar
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as string]))

(defn short-string [] (gen/such-that #(> 15 (count %) 0) (gen/string-alphanumeric)))

(defn short-keyword-gen [] (s/with-gen keyword? #(gen/fmap keyword (short-string))))

(defn short-string-gen [] (s/with-gen string? short-string))


;; common

(s/def ::module-name (short-string-gen))
(s/def ::category (short-keyword-gen))


;; abstract

(s/def ::flags (s/map-of (short-keyword-gen) (short-keyword-gen)))

(s/def ::categories (s/coll-of ::category :min-count 1 :gen-max 4 :kind set?))

(s/def ::arguments (s/coll-of ::category :min-count 1 :gen-max 4))

(s/def ::return ::category)

(s/def ::function (s/keys :req [::name ::arguments ::return]))

(s/def ::functions (s/coll-of ::function :min-count 1))

(s/def ::abstract-grammar (s/keys :req [::module-name ::flags ::categories ::functions]))

;; concrete

(s/def ::body (s/coll-of (s/or :literal (short-string-gen) :variable (short-keyword-gen))))

(s/def ::of ::module-name)

(s/def ::function-name (short-string-gen))

(s/def ::lin-types
  (s/map-of ::category
            (s/cat :var-name #{:s :n} :var-type #{:str :number})))

(s/def ::syntax (short-string-gen))

(s/def ::lin-function (s/keys :req [::function-name ::syntax]))

(s/def ::lins (s/coll-of ::lin-function))

(s/def ::concrete-grammar (s/keys :req [::module-name ::of ::lin-types ::lins]))
