(ns api.graphql.translate.document-plan
  (:require [api.utils :refer [read-mapper]]
            [jsonista.core :as json]))

(defn schema->dp [{:keys [uid name kind blocklyXml documentPlan dataSampleId dataSampleRow]}]
  {:uid           uid
   :name          name
   :kind          kind
   :blocklyXml    blocklyXml
   :documentPlan  (json/read-value documentPlan read-mapper)
   :dataSampleId  dataSampleId
   :dataSampleRow dataSampleRow})

(defn dp->schema [dp]
  (update dp :documentPlan json/write-value-as-string))
