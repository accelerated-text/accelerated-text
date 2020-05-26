(ns acc-text.nlg.gf.utils
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.set :as set]
            [clojure.string :as str]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [ubergraph.core :as uber])
  (:import (java.io PushbackReader)))

(defn read-edn [f]
  (with-open [r (io/reader f)]
    (edn/read (PushbackReader. r))))

(defn get-rgl-functions-from-resource [& paths]
  (->> paths
       (mapcat #(str/split (slurp %) #"\n\n"))
       (map #(let [[desc & items] (str/split-lines (str/trim %))
                   [_ type text] (re-find #"//\s+(.+?)\s+-\s+(.+)" desc)]
               {:type        (or type (second (re-find #"//\s+(.+)" desc)))
                :description (or text "")
                :functions   (mapv (fn [line]
                                     (let [name (second (re-find #"^([\w()]+)" line))
                                           line (str/trim (subs line (count name)))
                                           body (str/trim (second (re-find #"([\w()]+ (\s*-> [\w()]+)*)" line)))
                                           type (mapv str/trim (str/split body #"\s+->\s+"))
                                           line (str/trim (subs line (count body)))]
                                       {:function name
                                        :type     type
                                        :example  line}))
                                   items)}))
       (group-by :type)
       (vals)
       (map (fn [instances]
              (let [[{:keys [type description]}] instances]
                {:type        type
                 :description description
                 :module      "Syntax"
                 :functions   (into [] (mapcat :functions instances))})))
       (sort-by :type)
       (into [])))

(defn spit-rgl-functions [fns output-path]
  (doseq [{type :type :as f} fns]
    (spit (format (str output-path "/%s.edn") type) (with-out-str (pprint f)))))

(defn read-rgl-functions [& paths]
  (->> paths
       (map #(rest (file-seq (io/file %))))
       (apply concat)
       (map read-edn)))

(defn- remove-optional-params [params]
  (remove #(re-matches #"^\(.+\)$" %) params))

(defn- find-position [params]
  (some (fn [[index param]]
          (when-not (re-matches #"^\(.+\)$" param) index))
        (map-indexed #(vector %1 %2) params)))

(defn- find-single-arity-functions [fns]
  (reduce (fn [acc {:keys [module functions]}]
            (concat acc (sequence
                          (comp
                            (map #(set/rename-keys % {:type :params}))
                            (map #(assoc % :type (last (:params %))))
                            (map #(assoc % :position (find-position (:params %))))
                            (map #(update % :params (comp remove-optional-params butlast)))
                            (remove #(not= 1 (count (:params %))))
                            (map #(set/rename-keys % {:params :from}))
                            (map #(update % :from first))
                            (map #(assoc % :module module))
                            (map #(select-keys % [:function :type :position :from :module])))
                          functions)))
          []
          fns))

(defn- apply-function-filter [fns]
  (filter #(re-matches #"^mk.+$" (:function %)) fns))

(defn- group-single-arity-function-params [fns]
  (reduce-kv (fn [acc _ fns]
               (concat acc (map (fn [group]
                                  (assoc (first group) :from (map :from group)))
                                (vals (group-by :type fns)))))
             []
             (group-by :function fns)))

(defn make-transformation-graph [& paths]
  (let [fns (-> (apply read-rgl-functions paths) (find-single-arity-functions) (apply-function-filter) (group-single-arity-function-params))
        type->fns (group-by :type fns)]
    (apply uber/digraph (->> fns
                             (mapcat (fn [{:keys [function type from module position]}]
                                       (cons
                                         [^:node function {:module module :type type :position position}]
                                         (cons
                                           [^:edge function type]
                                           (->> (for [type from
                                                      {from-function :function from-type :type} (cons {:type type}
                                                                                                      (type->fns type))]
                                                  [^:edge (or from-function from-type) function])
                                                (filter #(not= (first %) (second %))))))))
                             (concat (into #{} (concat
                                                 (map :type fns)
                                                 (mapcat :from fns))))))))
(def op-map
  {"N"   #{"mkN" "mkN2" "mkN3" "mkCN" "mkPN"}
   "V"   #{"mkV" "mkV2" "mkV2A" "mkV3" "mkVA" "mkVQ" "mkVS" "mkVV"}
   "A"   #{"mkA" "mkA2"}
   "Adv" #{"mkAdv"}})

(defn find-paths [g]
  (letfn [(normalize-op [x]
            (cond
              (contains? #{"N" "N2" "N3" "CN" "PN" "NP"} x) "N"
              (contains? #{"V" "V2" "V2A" "V3" "VA" "VQ" "VS" "VV" "VP"} x) "V"
              (contains? #{"A" "A2" "AP"} x) "A"
              :else x))
          (get-required-ops [src]
            (case (normalize-op src)
              "N" #{"mkNP"}
              "V" #{"mkVP"}
              "A" #{"mkAP"}
              "Str" #{"mkCN" "mkN"}
              #{}))
          (evaluate-path [src dest path]
            (let [ops (get op-map (or (normalize-op src) (normalize-op dest)))
                  required-ops (get-required-ops src)
                  desired-ops (set/union #{"mkUtt"} ops)
                  undesired-ops (set/union #{"mkImp" "mkQS" "mkQCl" "mkPost" "mkVPSlash"}
                                           (set/difference (apply set/union (vals op-map)) ops))]
              [(if (set/subset? required-ops (set path)) 0 1)
               (if (zero? (count (set/intersection undesired-ops (set path)))) 0 1)
               (if (zero? (count (set/intersection desired-ops (set path)))) 1 0)
               (count path)]))
          (path-ended? [path]
            (and (< 1 (count path)) (nil? (:type (attrs g (last path))))))
          (traverse [path]
            (let [successors (graph/successors g (last path))]
              (if (or (nil? successors) (path-ended? path))
                (str/join "\n" path)
                (map traverse (for [s (remove #(contains? (set path) %) successors)]
                                (conj path s))))))
          (path->sg [path]
            (letfn [(->id [index] (keyword (format "%02d" (inc index))))]
              #::sg{:relations (map (fn [index function last-function]
                                      (let [{:keys [type]} (attrs g function)
                                            {:keys [position]} (attrs g last-function)]
                                        {:from     (->id index)
                                         :to       (->id (inc index))
                                         :role     :arg
                                         :index    position
                                         :category type}))
                                    (range) (rest (reverse path)) (butlast (reverse path)))
                    :concepts  (map (fn [index function]
                                      (let [{:keys [type module]} (attrs g function)]
                                        {:id       (->id index)
                                         :type     :operation
                                         :name     function
                                         :category type
                                         :module   module}))
                                    (range) (reverse path))}))]
    (->> (graph/nodes g)
         (remove #(or (= "Text" %) (some? (:type (attrs g %)))))
         (map (comp traverse vector))
         (flatten)
         (map str/split-lines)
         (group-by #(vector (first %) (last %)))
         (reduce-kv (fn [m [src dest] paths]
                      (cond-> m
                              (and
                                (not= [src dest] ["Str" "Utt"])
                                (not= src dest)) (assoc [src dest] (first (map (comp path->sg rest butlast)
                                                                               (sort-by #(evaluate-path src dest %) paths))))))
                    {}))))

(defn save-graph [g output-path]
  (uber/viz-graph g {:save {:filename output-path :format :png}}))

(defn save-paths [paths output-path]
  (spit output-path (with-out-str (pprint paths))))

(def mod-position
  (-> ["Prep" "Digits" "Det" "IDet" "Temp" "Card" "IP" "Num"
       "N" "V" "Adv" "CN" "NP" "AP" "VP" "IAdv" "VPSlash"
       "Cl" "ClSlash" "RCl" "QCl" "S" "RS" "Text"]
      (zipmap (range))))

(defn sort-modifiers [xs]
  (sort-by #(get mod-position (:category %) 99) xs))

(defn find-modifiers [& paths]
  (->> paths
       (apply read-rgl-functions)
       (mapcat (fn [{:keys [module functions]}] (map #(assoc % :module module) functions)))
       (filter (fn [{:keys [type function]}] (and
                                               (= 3 (count (filter #(re-matches #"\p{L}+" %) type)))
                                               (re-matches #"^mk.+" function)
                                               (not (re-matches #"^mkList.+" function))
                                               (not= "Str" (first type) (second type)))))
       (map (fn [{:keys [function type module]}]
              (let [[x y z] (filter #(re-matches #"\p{L}+" %) type)]
                {[x y] (list {:type :operation :name function :category z :module module})})))
       (apply merge-with (comp sort-modifiers distinct concat))))
