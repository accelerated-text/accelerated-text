(ns acc-text.nlg.gf.grammar.cf
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.gf.string-utils :as su]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]))

(defn join-relation-ids [relations]
  (if (seq relations)
    (->> relations
         (map (comp #(str "x" %) name ::sg/to))
         (str/join " "))
    "\"\""))

(defmulti build-fragment ::sg/type)

(defmethod build-fragment :document-plan [{relations ::sg/relations} _]
  (format "Document. S ::= %s;" (join-relation-ids relations)))

(defmethod build-fragment :segment [{id ::sg/id relations ::sg/relations} _]
  (when (seq relations)
    (format "x%s ::= %s;" (name id) (join-relation-ids relations))))

(defmethod build-fragment :amr [{id ::sg/id value ::sg/value relations ::sg/relations} {amr :amr}]
  (let [syntax (->> (keyword value) (get amr) (:frames) (map :syntax))
        function (some (fn [{role ::sg/role to ::sg/to}]
                         (when (= :function role) (name to)))
                       relations)
        name->id (reduce (fn [m {to ::sg/to role ::sg/role {attr-name ::sg/name} ::sg/attributes}]
                           (cond-> m (and (not= :function role) (some? attr-name)) (assoc (str/lower-case attr-name) (str "x" (name to)))))
                         {}
                         relations)]
    (for [[i instance] (zipmap (rest (range)) syntax)]
      (format "%sV%s. x%s ::= %s;" (str/capitalize value) i (name id) (str/join " " (for [{value :value} instance]
                                                                                      (or (get name->id (when value (str/lower-case value)))
                                                                                          (when value (format "\"%s\"" value))
                                                                                          (str "x" function))))))))

(defmethod build-fragment :data [{id ::sg/id value ::sg/value relations ::sg/relations} _]
  (if-not (seq relations)
    (format "Data. x%s ::= \"{{%s}}\";" (name id) value)
    (format "DataMod%d. x%s ::= %s \"{{%s}}\";" (count relations) (name id) (join-relation-ids relations) value)))

(defmethod build-fragment :quote [{id ::sg/id value ::sg/value relations ::sg/relations} _]
  (if-not (seq relations)
    (format "Quote. x%s ::= \"%s\";" (name id) (su/escape-string value))
    (format "QuoteMod%d. x%s ::= x%s \"%s\";" (count relations) (name id) (join-relation-ids relations) (su/escape-string value))))

(defmethod build-fragment :dictionary-item [{id ::sg/id value ::sg/value {attr-name ::sg/name} ::sg/attributes relations ::sg/relations} {dictionary :dictionary}]
  (for [value (set (cons attr-name (get dictionary value)))]
    (if-not (seq relations)
      (format "Item. x%s ::= \"%s\";" (name id) value)
      (format "ItemMod%d. x%s ::= \"%s\" %s;" (count relations) (name id) value (join-relation-ids relations) (su/escape-string value)))))

(defmethod build-fragment :sequence [{id ::sg/id relations ::sg/relations} _]
  (when (seq relations)
    (format "x%s ::= %s;" (name id) (join-relation-ids relations))))

(defmethod build-fragment :shuffle [{id ::sg/id relations ::sg/relations} _]
  (for [p (permutations relations)]
    (format "x%s ::= %s;" (name id) (join-relation-ids p))))

(defmethod build-fragment :synonyms [{id ::sg/id relations ::sg/relations} _]
  (if (seq relations)
    (for [{to ::sg/to} relations]
      (format "x%s ::= x%s;" (name id) (name to)))
    (format "x%s ::= \"\";" (name id))))

(defn build [{relations ::sg/relations concepts ::sg/concepts} context]
  (let [relation-map (group-by ::sg/from relations)]
    (->> concepts
         (map #(assoc % ::sg/relations (get relation-map (::sg/id %) [])))
         (map #(build-fragment % context))
         (flatten))))
