(ns graphql.domain.document-plan
  (:require [data-access.entities.document-plan :as document-plan]))

(defn get-document-plan [_ {:keys [id]} _]
  (document-plan/get id))

(defn delete-document-plan [_ {:keys [id]} _]
  (document-plan/delete id))

(defn list-document-plans [_ {:keys [offset limit] :or {offset 0 limit 20}} _]
  (let [items (document-plan/list)]
    {:items      (->> items (drop offset) (take limit))
     :limit      limit
     :offset     offset
     :totalCount (count items)}))

(defn add-document-plan [_ {:keys [id] :as args} _]
  (document-plan/add id args))

(defn update-document-plan [_ {:keys [id] :as args} _]
  (document-plan/update id args))
