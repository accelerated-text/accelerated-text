(ns api.graphql.translate.document-plan
  (:require [api.utils :refer [read-mapper]]
            [clojure.string :as str]
            [jsonista.core :as json]))

(defn schema->dp [{:keys [id uid name kind examples blocklyXml documentPlan dataSampleId dataSampleRow dataSampleMethod]}]
  {:id               id
   :uid              uid
   :name             name
   :kind             kind
   :examples         (remove str/blank? examples)
   :blocklyXml       blocklyXml
   :documentPlan     (json/read-value documentPlan read-mapper)
   :dataSampleId     dataSampleId
   :dataSampleRow    dataSampleRow
   :dataSampleMethod dataSampleMethod})

(defn dp->schema [dp]
  (update dp :documentPlan json/write-value-as-string))

(defn structured-dp->schema [{id                 :document-plan/id
                              uid                :document-plan/uid
                              name               :document-plan/name
                              kind               :document-plan/kind
                              blockly-xml        :document-plan/blockly-xml
                              document-plan      :document-plan/document-plan
                              data-sample-id     :document-plan/data-sample-id
                              data-sample-row    :document-plan/data-sample-row
                              data-sample-method :document-plan/data-sample-method :as dp}]
  {:id               id
   :uid              uid
   :name             name
   :kind             kind
   :blocklyXml       blockly-xml
   :documentPlan     (json/write-value-as-string document-plan)
   :dataSampleId     data-sample-id
   :dataSampleRow    data-sample-row
   :dataSampleMethod data-sample-method})
