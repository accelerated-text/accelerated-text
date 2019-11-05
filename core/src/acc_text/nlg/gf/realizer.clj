(ns acc-text.nlg.gf.realizer
  (:require [clojure.string :as string]))

(defn data-morphology-value [value] (format "{{%s}}" (string/upper-case value)))

(defn gf-syntax-item [syntactic-function category syntax]
  (format "%s. %s ::= %s;" (string/capitalize syntactic-function) category syntax))

(defn gf-morph-item [syntactic-function category syntax]
  (format "%s. %s ::= \"%s\";" (string/capitalize syntactic-function) category syntax))

(defn drop-non-semantic-parts [{:keys [concepts relations]}]
  (assoc {}
         :concepts (remove #(get #{:segment :document-plan} (:type %)) concepts)
         :relations (remove #(get #{:segment :instance} (:role %)) relations)))

(defn concepts-with-type [{concepts :concepts} concept-type]
  (filter (fn [{:keys [type]}] (= concept-type type)) concepts))

(defn relations-with-role [{relations :relations} relation-role]
  (filter (fn [{:keys [role]}] (= role relation-role)) relations))

(defn root-relation [{:keys [relations]} concept-table]
  (let [{:keys [to]} (->> relations
                          (filter (fn [{:keys [role]}] (= :segment role)))
                          (first))
        root-concept-rel (->> relations
                              (filter (fn [{:keys [from]}] (= to from)))
                              ;;FIXME. For now I assume that only one AMR will be present in Segment
                              (first))]
    (get concept-table (:to root-concept-rel))))

(defn start-category-graph
  "Get the sub-graph immediately bellow starting (the one under Segment) category"
  [semantic-graph concept-table]
  (let [{start-id :id :as start-cat} (root-relation semantic-graph concept-table)]
    {:concepts start-cat
     :relations (filter (fn [{:keys [from]}] (= from start-id)) (:relations semantic-graph))}))

(defn concepts->id-concept
  "Take semantic graph and produce a map of concept id to a concept item.
  Useful when later we do analysis based on relations where only concept ID is present"
  [{:keys [concepts]}]
  (reduce (fn [agg c]
            (assoc agg (:id c) c))
          {} concepts))

(defn relations-with-concepts
  "Take graph relation triplet and instead of just ID embed the full concept map.
  Going from [from-id to-id] to [from-concept-map to-concept-map]"
  [semantic-graph concept-table edge-role]
  (reduce (fn [agg {:keys [from to]}]
            (conj agg [(get concept-table from) (get concept-table to)]))
          []
          (relations-with-role semantic-graph edge-role)))

(defn modifier->gf [semantic-graph concept-table]
  (let [modifiers (relations-with-concepts semantic-graph concept-table :modifier)]
    (when (seq modifiers)
      (map (fn [[_ {{name :name} :attributes}]] (gf-morph-item name "A" name))
           modifiers))))

(defn data->gf [semantic-graph]
  (map (fn [{value :value}]
         (gf-morph-item value "NP" (data-morphology-value value)))
       (concepts-with-type semantic-graph :data)))

(defn start-category->gf [{:keys [relations concepts]}]
  ;;in order to decide which GF to generate we do not need complete concept/relation data
  ;;for pattern matching only their types are needed
  (let [concept-pattern  (set (map :type concepts))
        relation-pattern (set (map :role relations))]
    (cond
      ;;Data concept only graph
      (and (= concept-pattern #{:data}) (empty? relation-pattern))
      [(gf-syntax-item "Phrase" "S" "NP")]

      ;;Adverbial phrase only graph
      (and (= concept-pattern #{:data :dictionary-item}) (= relation-pattern #{:modifier}))
      [(gf-syntax-item "Phrase" "S" "AP")
       (gf-syntax-item "Compl-a" "AP" "A NP")]

      ;;Probably need to throw an error, we can not have unresolved start cats
      :else nil)))

(defn dp->grammar [dp]
  (let [sem-graph (drop-non-semantic-parts dp)
        concept-table (concepts->id-concept sem-graph)]
    (concat
      (start-category->gf sem-graph)
      (data->gf sem-graph)
      (modifier->gf sem-graph concept-table))))

(defn write-grammar
  "Debug function to spit grammar to a file"
  [rgl file-name]
  (let [out-file (format "grammars/gf/%s.cf" file-name)]
    (clojure.java.io/delete-file out-file true)
    (doseq [item rgl]
      (spit out-file (str item "\n") :append true))))
