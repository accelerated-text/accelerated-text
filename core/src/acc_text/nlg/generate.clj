(ns acc-text.nlg.generate
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defmulti build-grammar-fragment ::sg/type)

(defmethod build-grammar-fragment :document-plan [{relations ::sg/relations}]
  (format "S = %s" (str/join " " (map ::sg/id relations))))

(defmethod build-grammar-fragment :segment [{id ::sg/id relations ::sg/relations}]
  (format "%s = %s" (name id) (str/join " " (map ::sg/id relations))))

(defmethod build-grammar-fragment :amr [{id ::sg/id relations ::sg/relations {syntax ::sg/syntax} ::sg/attributes}]
  (let [function nil
        name->id (reduce (fn [m {to ::sg/to role ::sg/role {attr-name ::sg/name} ::sg/attributes}]
                           (cond-> m (and (not= :function role) (some? attr-name)) (assoc (str/lower-case attr-name) (name to))))
                         {}
                         relations)]
    (for [instance syntax]
      (format "%s = %s" (name id) (str/join " " (for [{pos :pos value :value} (log/spy instance)]
                                                  (or (get name->id (when value (str/lower-case value)))
                                                      (when value (format "\"%s\"" value))
                                                      function)))))))

(defmethod build-grammar-fragment :data [{id ::sg/id value ::sg/value}]
  (format "%s = \"{{%s}}\"" (name id) value))

(defmethod build-grammar-fragment :quote [{id ::sg/id value ::sg/value}]
  (format "%s = \"%s\"" value))

(defmethod build-grammar-fragment :dictionary-item [{id ::sg/id value ::sg/value members ::sg/members}]
  (for [v (cons value members)]
    (format "%s = \"%s\"" (name id) v)))

(defn build-grammar [{{relations ::sg/relations concepts ::sg/concepts :as graph} ::sg/graph}]
  (let [relation-map (group-by ::sg/from relations)]
    (->> concepts
         (map #(assoc % ::sg/relations (get relation-map (::sg/id %) [])))
         (map build-grammar-fragment)
         (flatten))))
