(ns graphql.domain.document-plan
  (:require [data-access.entities.document-plan :as dp]))

(defn get-document-plan [_ {:keys [id]} _]
  (dp/get-document-plan id))

(defn delete-document-plan [_ {:keys [id]} _]
  (dp/delete-document-plan id)
  true)

(defn list-document-plans [_ {:keys [offset limit] :or {offset 0 limit 20}} _]
  (let [items (dp/list-document-plans)]
    {:items      (->> items (drop offset) (take limit))
     :limit      limit
     :offset     offset
     :totalCount (count items)}))

(defn add-document-plan [_ args _]
  (dp/add-document-plan args))

(defn update-document-plan [_ {:keys [id] :as args} _]
  (dp/update-document-plan id args))
