(ns data.entities.document-plan.utils
  (:require [data.entities.document-plan.zip :as dp-zip]
            [data.entities.dictionary :refer [get-dictionary-item-category]]
            [clojure.zip :as zip]
            [clojure.java.io :as io]
            [clojure.data.xml :as xml]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn parse-blockly-xml [is]
  (try
    (xml/parse is)
    (catch Exception _
      (log/error "Failed to parse blockly xml."))))

(defn get-variable-labels [blockly-xml]
  (when (some? blockly-xml)
    (with-open [is (io/input-stream (.getBytes blockly-xml))]
      (let [{[{vars :content}] :content} (parse-blockly-xml is)]
        (reduce (fn [m {{var-id :id} :attrs
                        [var-name]   :content}]
                  (cond-> m
                          (and
                            (some? var-id)
                            (some? var-name)) (assoc var-id var-name)))
                {}
                vars)))))

(defn ensure-dictionary-item-categories [blockly-xml]
  (when (some? blockly-xml)
    (with-open [is (io/input-stream (.getBytes blockly-xml))]
      (loop [loc (zip/xml-zip (parse-blockly-xml is))]
        (if (zip/end? loc)
          (xml/emit-str (zip/root loc))
          (-> loc
              (zip/edit (fn [{{:keys [id pos]} :attrs :as node}]
                          (cond-> node
                                  (= "null" pos) (assoc-in [:attrs :pos] (get-dictionary-item-category id)))))
              (zip/next)
              (recur)))))))

(defn get-nodes-with-types [body types]
  (->> (dp-zip/make-zipper body)
       (iterate zip/next)
       (take-while (complement zip/end?))
       (map zip/node)
       (filter #(contains? types (:type %)))))

(defn find-examples [{body :documentPlan examples :examples blockly-xml :blocklyXml}]
  (let [labels (get-variable-labels blockly-xml)]
    (or (->> (get-nodes-with-types body #{"Define-var"})
             (filter #(and (= "Define-var" (:type %)) (= "*Description" (get labels (:name %)))))
             (map (comp :text :value))
             (remove str/blank?)
             (seq))
        (remove str/blank? examples))))

(defn find-variables [{body :documentPlan} labels]
  (->> (get-nodes-with-types body #{"Define-var"})
       (map (fn [{var-id :name :as node}]
              {var-id (-> node
                          (assoc :label (get labels var-id))
                          (dissoc :value)
                          (vector))}))
       (apply merge-with concat)))