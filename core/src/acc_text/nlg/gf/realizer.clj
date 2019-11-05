(ns acc-text.nlg.gf.realizer
  (:require [clojure.string :as string]))

(defn data-morphology-value [{value :value}] (format "{{%s}}" (string/upper-case value)))

(defn gf-syntax-item [syntactic-function category syntax]
  (format "%s. %s ::= %s ;" (string/capitalize syntactic-function) category syntax))

(defn gf-morph-item [syntactic-function category syntax]
  (format "%s. %s ::= \"%s\" ;" (string/capitalize syntactic-function) category syntax))

(defn find-root-amr [{:keys [concepts relations]}]
  (let [root-amr-id (->> relations
                         (filter (fn [{:keys [role]}] (= :instance role)))
                         (map :to)
                         (set))]
    (filter (fn [{id :id}] (get root-amr-id id)) concepts)))

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
    [(get concept-table (:from root-concept-rel)) (get concept-table (:to root-concept-rel))]))

(defn gf-start-category [semantic-graph concept-table]
  (let [rel (root-relation semantic-graph concept-table)])
  )

(defn concepts->id-concept
  "Take semantic graph and produce a map of concept id to a concept item.
  Useful when later we do analysis based on relations where only concept ID is present"
  [{:keys [concepts]}]
  (reduce (fn [agg c]
            (assoc agg (:id c) c))
          {} concepts))

(defn relations-nodes [semantic-graph concept-table edge-role]
  (reduce (fn [agg {:keys [from to]}]
            (conj agg [(get concept-table from) (get concept-table to)]))
          []
          (relations-with-role semantic-graph edge-role)))

(defn modifier->gf [semantic-graph concept-table]
  (let [modifiers (relations-nodes semantic-graph concept-table :modifier)]
    (when (seq modifiers)
      (cons "ComplA. AP ::= A NP;"
            (map (fn [[_ {{name :name} :attributes}]]
                   (gf-syntax-item name "A" (data-morphology-value name)))
                 modifiers)))))

(defn data->gf [semantic-graph]
  (map (fn [{value :value}] (gf-morph-item value "NP" value))
       (concepts-with-type semantic-graph :data)))

(defn dp->rgl [dp]
  (let [sem-graph (drop-non-semantic-parts dp)
        concept-table (concepts->id-concept sem-graph)]

    (concat
      (data->gf sem-graph)
      (modifier->gf sem-graph concept-table))




    #_(cond
      (AP-only-graph? sem-graph)
      (let [np (first-by-type sem-graph :data)
            adj (first-by-type sem-graph :dictionary-item)]
        [(gf-syntax-item "Pred" "S" "AP")
         (gf-syntax-item "Compl" "AP" "A NP")
         ;;TIXME words must come from DP Instance
         (gf-morph-item "GOOD" "A" "good")
         (gf-morph-item "GOOD" "A" "nice")
         (gf-morph-item (data-syntactic-function-name np) "NP" (data-morphology-value np))])

      (NP-only-graph? sem-graph)
      (let [predicate (-> sem-graph :concepts first)]
        [(gf-syntax-item "Pred" "S" "NP")
         (gf-morph-item (data-syntactic-function-name predicate) "NP" (data-morphology-value predicate))]))))

(defn write-rgl [rgl file-name]
  (let [out-file (format "grammars/gf/%s.cf" file-name)]
    (clojure.java.io/delete-file out-file true)
    (doseq [item rgl]
      (spit out-file (str item "\n") :append true))))
