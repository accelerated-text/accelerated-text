(ns data.entities.document-plan
  (:require [api.config :refer [conf]]
            [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate document-plans-db :start (db/db-access :blockly conf))

(defn list-document-plans
  ([]
   (db/scan! document-plans-db {}))
  ([kind]
   (filter #(= kind (:kind %)) (db/scan! document-plans-db {}))))

(defn get-document-plan [document-plan-id]
  (db/read! document-plans-db document-plan-id))

(defn delete-document-plan [document-plan-id]
  (db/delete! document-plans-db document-plan-id))

(defn add-document-plan
  ([document-plan] (add-document-plan document-plan (utils/gen-rand-str 16)))
  ([document-plan provided-id]
   (db/write! document-plans-db provided-id document-plan true)))

(defn update-document-plan [document-plan-id document-plan]
  (db/update! document-plans-db document-plan-id document-plan))

(defn get-variable-names [{blockly-xml :blocklyXml}]
  (with-open [is (io/input-stream (.getBytes blockly-xml))]
    (let [{[{vars :content}] :content} (xml/parse is)]
      (reduce (fn [m {{var-id :id} :attrs
                      [var-name]   :content}]
                (assoc m var-id var-name))
              {}
              vars))))

(defn document-plan-path []
  (or (System/getenv "DOCUMENT_PLANS") "grammar/document_plans"))

(defn initialize []
  (doseq [{id :id :as dp} (->> (document-plan-path) (utils/list-files) (map utils/read-json))]
    (-> dp (update :documentPlan utils/read-json-str) (add-document-plan id))))
