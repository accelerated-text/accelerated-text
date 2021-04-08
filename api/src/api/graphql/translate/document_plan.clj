(ns api.graphql.translate.document-plan
  (:require [api.graphql.domain.concept :refer [get-amr-concept]]
            [api.utils :refer [read-mapper]]
            [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clojure.zip :as zip]
            [data.entities.dictionary :refer [get-dictionary-item-category]]
            [jsonista.core :as json]))

(defn dp-zipper [m]
  (zip/zipper
    (fn [x] (or (map? x) (sequential? x)))
    seq
    (fn [p xs]
      (if (isa? (type p) clojure.lang.MapEntry)
        (into [] xs)
        (into (empty p) xs)))
    m))

(defn parse-blockly-xml [is]
  (try
    (xml/parse is)
    (catch Exception _
      (log/error "Failed to parse blockly xml."))))

(defn ensure-blockly-categories [blockly-xml]
  (letfn [(edit [{{:keys [id concept_id pos concept_kind]} :attrs :as node}]
            (cond-> node
                    (= "null" pos) (assoc-in [:attrs :pos] (get-dictionary-item-category id))
                    (= "null" concept_kind) (assoc-in [:attrs :concept_kind] (:kind (get-amr-concept concept_id)))))]
    (when (some? blockly-xml)
      (with-open [is (io/input-stream (.getBytes blockly-xml))]
        (loop [loc (zip/xml-zip (parse-blockly-xml is))]
          (if (zip/end? loc)
            (xml/emit-str (zip/root loc))
            (-> loc
                (zip/edit edit)
                (zip/next)
                (recur))))))))

(defn ensure-categories [document-plan]
  (letfn [(edit [node]
            (if (map? node)
              (let [{:keys [type kind itemId conceptId]} node]
                (cond
                  (and
                    (or (= "Dictionary-item" type)
                        (= "Dictionary-item-modifier" type))
                    (or (nil? kind) (= "null" kind))) (assoc node :kind (get-dictionary-item-category itemId))
                  (and
                    (= "AMR" type)
                    (or (nil? kind) (= "null" kind))) (assoc node :kind (:kind (get-amr-concept conceptId)))
                  :else node))
              node))]
    (loop [loc (dp-zipper document-plan)]
      (if (zip/end? loc)
        (zip/root loc)
        (-> loc
            (zip/edit edit)
            (zip/next)
            (recur))))))

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
