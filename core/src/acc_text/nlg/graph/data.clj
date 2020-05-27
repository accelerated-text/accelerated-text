(ns acc-text.nlg.graph.data
  (:require [acc-text.nlg.graph.utils :refer [find-nodes get-in-edge]]
            [acc-text.nlg.graph.dictionary-item :refer [get-dictionary-item add-dictionary-item]]
            [loom.attr :refer [attrs]]))

(defn get-data [data key]
  (if (contains? data key)
    (get data key)
    (throw (Exception. (format "Missing value for data cell: `%s`" key)))))

(defn find-data-category [g node-id]
  (prn "XL " (->> node-id (get-in-edge g) (attrs g)))
  (let [edge-attrs (->> node-id (get-in-edge g) (attrs g))
        category (get edge-attrs :category
                      (if (= :modifier (:role edge-attrs))
                        "A" "N"))]
    (prn "CAt: " category)
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
              (if-not (contains? dictionary [value category])
                (update-in g [:attrs node-id] #(merge % {:type :quote :value value}))
                (add-dictionary-item g node-id
                                     (get-dictionary-item dictionary lang value category)))))
          g
          (concat (find-nodes g {:type :data}))))
