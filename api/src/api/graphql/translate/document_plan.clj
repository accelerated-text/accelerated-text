(ns api.graphql.translate.document-plan
  (:require [cheshire.core :as ch]))

(defn schema->dp [{:keys [id uid name blocklyXml documentPlan dataSampleId dataSampleRow]}]
  {:id            id
   :uid           uid
   :name          name
   :blocklyXml    blocklyXml
   :documentPlan  (ch/decode documentPlan true)
   :dataSampleId  dataSampleId
   :dataSampleRow dataSampleRow})

(defn dp->schema [dp]
  (update dp :documentPlan ch/encode))
