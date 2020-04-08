(ns acc-text.nlg.gf.utils
  (:require [clojure.edn :as edn]
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

(defn- find-single-arity-functions [fns]
  (reduce (fn [acc {:keys [module functions]}]
            (concat acc (sequence
                          (comp
                            (map #(set/rename-keys % {:type :params}))
                            (map #(assoc % :type (last (:params %))))
                            (map #(update % :params (comp remove-optional-params butlast)))
                            (remove #(not= 1 (count (:params %))))
                            (map #(set/rename-keys % {:params :from}))
                            (map #(update % :from first))
                            (map #(assoc % :module module))
                            (map #(select-keys % [:function :type :from :module])))
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
                             (mapcat (fn [{:keys [function type from module]}]
                                       (cons
                                         [^:node function {:module module :type type}]
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

(defn find-paths [g]
  (letfn [(path-ended? [path]
            (and (< 1 (count path)) (nil? (:type (attrs g (last path))))))
          (traverse [path]
            (let [successors (graph/successors g (last path))]
              (if (or (nil? successors) (path-ended? path))
                (str/join "\n" path)
                (map traverse (for [s (remove #(contains? (set path) %) successors)]
                                (conj path s))))))
          (add-attributes [path]
            (map (fn [function]
                   (let [{:keys [type module]} (attrs g function)]
                     [module function type]))
                 path))]
    (->> (graph/nodes g)
         (remove #(or (= "Text" %) (some? (:type (attrs g %)))))
         (map (comp traverse vector))
         (flatten)
         (map str/split-lines)
         (group-by #(vector (first %) (last %)))
         (reduce-kv (fn [m [src dest] paths]
                      (cond-> m
                              (not= src dest) (assoc [src dest] (mapv (comp add-attributes rest butlast)
                                                                      (sort-by count paths)))))
                    {}))))

(defn save-graph [g output-path]
  (uber/viz-graph g {:save {:filename output-path :format :png}}))

(defn save-paths [paths output-path]
  (spit output-path (with-out-str (pprint paths))))
