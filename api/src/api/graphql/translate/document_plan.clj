(ns api.graphql.translate.document-plan
  (:require [api.utils :refer [read-mapper]]
            [clojure.string :as str]
            [data.entities.document-plan.utils :as dp-utils]
            [data.entities.dictionary :as dict]
            [jsonista.core :as json]))

(defn schema->dp [{:keys [id uid name kind examples blocklyXml documentPlan dataSampleId dataSampleRow dataSampleMethod]}]
  {:id               id
   :uid              uid
   :name             name
   :kind             kind
   :examples         (remove str/blank? examples)
   :blocklyXml       (dp-utils/ensure-dictionary-item-categories blocklyXml)
   :documentPlan     (json/read-value documentPlan read-mapper)
   :dataSampleId     dataSampleId
   :dataSampleRow    dataSampleRow
   :dataSampleMethod dataSampleMethod})

(defn dp->schema [dp]
  (update dp :documentPlan json/write-value-as-string))
