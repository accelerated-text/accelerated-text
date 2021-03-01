(ns api.graphql.translate.document-plan
  (:require [api.utils :refer [read-mapper]]
            [jsonista.core :as json]))

(defn schema->dp [{:keys [id uid name kind blocklyXml documentPlan dataSampleId dataSampleRow dataSampleMethod]}]
  {:id               id
   :uid              uid
   :name             name
   :kind             kind
   :blocklyXml       blocklyXml
   :documentPlan     (json/read-value documentPlan read-mapper)
   :dataSampleId     dataSampleId
   :dataSampleRow    dataSampleRow
   :dataSampleMethod dataSampleMethod})

(defn dp->schema [dp]
  (update dp :documentPlan json/write-value-as-string))
