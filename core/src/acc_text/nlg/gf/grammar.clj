(ns acc-text.nlg.gf.grammar
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.gf.string-utils :as su]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn join-relation-ids [relations]
  (->> relations
       (map (comp #(str "x" %) name ::sg/to))
       (str/join " ")))

(defmulti build-fragment ::sg/type)

(defmethod build-fragment :document-plan [{relations ::sg/relations}]
  (format "Document. S ::= %s;" (join-relation-ids relations)))

(defmethod build-fragment :segment [{id ::sg/id relations ::sg/relations}]
  (format "Segment. x%s ::= %s;" (name id) (join-relation-ids relations)))

(defmethod build-fragment :amr [{id ::sg/id value ::sg/value relations ::sg/relations {syntax ::sg/syntax} ::sg/attributes}]
  (let [function (some (fn [{role ::sg/role to ::sg/to}]
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

(defmethod build-fragment :data [{id ::sg/id value ::sg/value relations ::sg/relations}]
  (if-not (seq relations)
    (format "Data. x%s ::= \"{{%s}}\";" (name id) value)
    (format "DataMod%d. x%s ::= %s \"{{%s}}\";" (count relations) (name id) (join-relation-ids relations) value)))

(defmethod build-fragment :quote [{id ::sg/id value ::sg/value relations ::sg/relations}]
  (if-not (seq relations)
    (format "Quote. x%s ::= \"%s\";" (name id) (su/escape-string value))
    (format "QuoteMod%d. x%s ::= x%s \"%s\";" (count relations) (name id) (join-relation-ids relations) (su/escape-string value))))

(defmethod build-fragment :dictionary-item [{id ::sg/id members ::sg/members {attr-name ::sg/name} ::sg/attributes relations ::sg/relations}]
  (for [value (set (cons attr-name members))]
    (if-not (seq relations)
      (format "Item. x%s ::= \"%s\";" (name id) value)
      (format "ItemMod%d. x%s ::= \"%s\" %s;" (count relations) (name id) value (join-relation-ids relations) (su/escape-string value)))))

(defn build [{relations ::sg/relations concepts ::sg/concepts}]
  (let [relation-map (group-by ::sg/from relations)]
    (->> concepts
         (map #(assoc % ::sg/relations (get relation-map (::sg/id %) [])))
         (map build-fragment)
         (flatten))))

(s/fdef build
        :args (s/cat :semantic-graph ::sg/graph)
        :ret (s/coll-of string? :min-count 2))
