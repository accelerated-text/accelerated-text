(ns translate.document-plan
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as ch]))

(defn encode-dp [dp] (ch/encode dp))

(defn decode-dp [dp] (ch/decode dp true))

(defn schema->dp
  [{:keys [id uid name blocklyXml documentPlan dataSampleId dataSampleRow createdAt updatedAt updateCount]}]
  {:id            id
   :uid           uid
   :name          name
   :blocklyXml    blocklyXml
   :documentPlan  (decode-dp documentPlan)
   :dataSampleId  dataSampleId
   :dataSampleRow dataSampleRow})

(defn dp->schema [dp] (update dp :documentPlan encode-dp))
