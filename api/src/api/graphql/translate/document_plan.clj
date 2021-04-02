(ns api.graphql.translate.document-plan
  (:require [api.graphql.domain.concept :refer [get-amr-concept]]
            [api.utils :refer [read-mapper]]
            [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clojure.zip :as zip]
            [data.entities.dictionary :refer [get-dictionary-item-category]]
            [data.entities.document-plan.zip :as dp-zip]
            [jsonista.core :as json]))

(defn parse-blockly-xml [is]
  (try
    (xml/parse is)
    (catch Exception _
      (log/error "Failed to parse blockly xml."))))

(defn ensure-blockly-categories [blockly-xml]
  (when (some? blockly-xml)
    (with-open [is (io/input-stream (.getBytes blockly-xml))]
      (loop [loc (zip/xml-zip (parse-blockly-xml is))]
        (if (zip/end? loc)
          (xml/emit-str (zip/root loc))
          (-> loc
              (zip/edit (fn [{{:keys [id concept_id pos concept_kind]} :attrs :as node}]
                          (cond-> node
                                  (= "null" pos) (assoc-in [:attrs :pos] (get-dictionary-item-category id))
                                  (= "null" concept_kind) (assoc-in [:attrs :concept_kind] (:kind (get-amr-concept concept_id))))))
              (zip/next)
              (recur)))))))

(defn ensure-categories [document-plan]
  (loop [loc (dp-zip/make-zipper document-plan)]
    (if (zip/end? loc)
      (zip/root loc)
      (-> loc
          (zip/edit #(cond
                       (and
                         (or (= "Dictionary-item" (:type %))
                             (= "Dictionary-item-modifier" (:type %)))
                         (or (nil? (:kind %)) (= "null" (:kind %)))) (assoc % :kind (get-dictionary-item-category (:itemId %)))
                       (and
                         (= "AMR" (:type %))
                         (or (nil? (:kind %)) (= "null" (:kind %)))) (assoc % :kind (:kind (get-amr-concept (:conceptId %))))
                       :else %))
          (zip/next)
          (recur)))))

(defn schema->dp [{:keys [id uid name kind examples blocklyXml documentPlan dataSampleId dataSampleRow dataSampleMethod]}]
  {:id               id
   :uid              uid
   :name             name
   :kind             kind
   :examples         (remove str/blank? examples)
   :blocklyXml       (ensure-blockly-categories blocklyXml)
   :documentPlan     (ensure-categories (json/read-value documentPlan read-mapper))
   :dataSampleId     dataSampleId
   :dataSampleRow    dataSampleRow
   :dataSampleMethod dataSampleMethod})

(defn dp->schema [dp]
  (update dp :documentPlan json/write-value-as-string))
