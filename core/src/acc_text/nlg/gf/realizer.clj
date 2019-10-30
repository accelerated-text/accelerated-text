(ns acc-text.nlg.gf.realizer
  (:require [clojure.string :as string]))

;; Pred.  S  ::= NP VP ;
;; Compl. VP ::= V2 NP ;
;; John.  NP ::= "John" ;
;; Mary.  NP ::= "Mary" ;
;; L.  V2 ::= "loves" ;
;; L.  V2 ::= "adores" ;

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

(defn NP-only-graph? [{concepts :concepts}]
  (and (= 1 (count concepts))
       (= :data (:type (first concepts)))))

(defn AP-only-graph? [{:keys [concepts relations]}]
  (and (= 2 (count concepts))
       (= :modifier (:role (first relations)))))

(defn AMR-only-graph? [{:keys [concepts relations] :as dp}]
  (let [amr (first-by-type dp :amr)]
    ))

(defn dp->rgl [dp]
  (let [sem-graph (drop-non-semantic-parts dp)]
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
