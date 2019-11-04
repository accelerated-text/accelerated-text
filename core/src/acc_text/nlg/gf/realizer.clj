(ns acc-text.nlg.gf.realizer
  (:require [clojure.string :as string]))

(defn data-morphology-value [{value :value}] (format "{{%s}}" (string/upper-case value)))
(defn data-syntactic-function-name [{value :value}] (string/upper-case value))

(defn gf-syntax-item [syntactic-function category syntax]
  (format "%s. %s ::= %s ;" syntactic-function category syntax))

(defn gf-morph-item [syntactic-function category syntax]
  (format "%s. %s ::= \"%s\" ;" syntactic-function category syntax))

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

(defn first-by-type [{concepts :concepts} concept-type]
  (some (fn [{:keys [type] :as c}] (when (= concept-type type) c)) concepts))

(defn all-by-type [{concepts :concepts} concept-type]
  (filter (fn [{:keys [type]}] (= concept-type type)) concepts))

(defn relations-with-role [{relations :relations} relation-role]
  (filter (fn [{:keys [role]}] (= role relation-role)) relations))

(defn NP-only-graph? [{concepts :concepts}]
  (and (= 1 (count concepts))
       (= :data (:type (first concepts)))))

(defn AP-only-graph? [{:keys [concepts relations]}]
  (and (= 2 (count concepts))
       (= :modifier (:role (first relations)))))

(defn AMR-only-graph? [{:keys [concepts relations] :as dp}]
  (let [amr (first-by-type dp :amr)]
    ))

(defn concepts->id-concept
  "Take semantic graph and produce a map of concept id to a concept item.
  Useful when later we do analysis based on relations where only concept ID is present"
  [{:keys [concepts]}]
  (reduce (fn [agg c]
            (assoc agg (:id c) c))
          {} concepts))

(defn modifier-relations [semantic-graph concept-table]
  (reduce (fn [agg {:keys [from to]}]
            (conj agg [(get concept-table from) (get concept-table to)]))
          []
          (relations-with-role semantic-graph :modifier)))

(defn modifier->gf [semantic-graph concept-table]
  (cons "ComplA. AP ::= A NP;"
        (map (fn [[_ {{name :name} :attributes}]]
               (gf-syntax-item name "A" name))
             (modifier-relations semantic-graph concept-table))))

(defn dp->rgl [dp]
  (let [sem-graph (drop-non-semantic-parts dp)
        concept-table (concepts->id-concept sem-graph)]
    (cond
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
