(ns data.datomic.entities.results
  (:require [data.spec.result :as result]
            [data.spec.result.row :as result-row]
            [data.spec.result.annotation :as result-annotation]
            [data.utils :refer [ts-now]]
            [datomic.api :as d]))

(defn transact-item [conn data-item]
  @(d/transact conn [(assoc data-item ::result/timestamp (ts-now))]))

(defn pull-entity [conn key]
  (d/pull (d/db conn)
          [::result/id
           ::result/status
           ::result/error-message
           ::result/timestamp
           {::result/rows [::result-row/id
                           ::result-row/text
                           ::result-row/language
                           ::result-row/enriched?
                           {::result-row/annotations [::result-annotation/id
                                                      ::result-annotation/idx
                                                      ::result-annotation/text]}]}]
          [::result/id key]))
