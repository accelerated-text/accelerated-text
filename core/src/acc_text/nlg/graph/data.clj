(ns acc-text.nlg.graph.data
  (:require [acc-text.nlg.graph.utils :refer [find-nodes get-in-edge]]
            [loom.attr :refer [attrs]]
            [clojure.tools.logging :as log]
            [clojure.string :as str]))

(defn get-data [data key]
  (if (contains? data key)
    (get data key)
    (throw (Exception. (format "Missing value for data cell: `%s`" key)))))

(defn find-data-category [g node-id]
  (let [edge-attrs (->> node-id (get-in-edge g) (attrs g))
        category (get edge-attrs :category
                      ;; in case the category is not supplied,
                      ;; make it 'A' if we have modifier, and 'N' otherwise
                      (if (= :modifier (:role edge-attrs)) "A" "N"))]
    (cond
      (contains? #{"A" "A2" "ACard" "AP"} category) "A"
      (contains? #{"AdA" "AdN" "AdV" "Adv" "CAdv"} category) "Adv"
      (contains? #{"CN" "N" "N2" "N3" "NP"} category) "N"
      (contains? #{"V" "V2" "V2A" "V2Q" "V2S" "V2V" "V3" "VA" "VP" "VPSlash" "VQ" "VS" "VV"} category) "V"
      :else category)))

(defn resolve-data [g {data :data dictionary :dictionary {lang "*Language"} :constants}]
  (reduce (fn [g [node-id {key :name}]]
            (let [category (find-data-category g node-id)
                  value (get-data data key)]
              (update-in g [:attrs node-id] #(merge % (if (contains? dictionary [value category])
                                                        {:type     :dictionary-item
                                                         :name     (format "%s_%s_%s" value category lang)
                                                         :label    value
                                                         :category category}
                                                        {:type :quote :value value})))))
          g
          (concat (find-nodes g {:type :data}))))
