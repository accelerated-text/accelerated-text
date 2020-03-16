(ns acc-text.nlg.graph.data
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.grammar.dictionary-item :refer [build-dictionary-item]]
            [acc-text.nlg.graph.utils :refer [find-nodes]]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]))

(defn add-existing-dictionary-item [g node-id dictionary-item]
  (let [{::dictionary-item/keys [category language forms attributes]} dictionary-item]
    (update-in g [:attrs node-id] #(merge % {:type       :dictionary-item
                                             :category   category
                                             :language   language
                                             :forms      forms
                                             :attributes (or attributes {})}))))

(defn resolve-by-category [g node-id value lang]
  (reduce (fn [g in-edge]
            (if-let [category (:category (attrs g in-edge))]
              (assoc-in g [:attrs node-id] {:type     :dictionary-item
                                            :category category
                                            :language lang
                                            :forms    (case lang
                                                        "Eng" (repeat 4 value)
                                                        "Rus" (repeat 13 value)
                                                        :else value)})
              (update-in g [:attrs node-id] #(assoc % :value value))))
          g
          (graph/in-edges g node-id)))

(defn resolve-data [g {:keys [data dictionary constants]}]
  (reduce (fn [g [node-id {name :name}]]
            (if-let [value (get data (keyword name))]
              (if (contains? dictionary value)
                (add-existing-dictionary-item g node-id (get dictionary value))
                (resolve-by-category g node-id value (get constants "*Language")))
              (graph/remove-nodes g node-id)))
          g
          (find-nodes g {:type :data})))
