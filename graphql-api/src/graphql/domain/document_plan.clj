(ns graphql.domain.document-plans
  (:require [data-access.entities.document-plans :as document-plans]))

(defn get-workspace [_ {:keys [id]} _]
  (document-plans/get-workspace id))

(defn delete-workspace [_ {:keys [id]} _]
  (document-plans/delete-workspace id))

(defn write-workspace [_ workspace _]
  (document-plans/write-workspace workspace))

(defn list-workspaces [_ {:keys [limit offset]} _]
  (let [workspaces (document-plans/list-workspaces)]
    {:items      workspaces
     :limit      limit
     :offset     0
     :totalCount (count workspaces)}))

(defn add-workspace [_ workspace _]
  (document-plans/add-workspace workspace))

(defn update-workspace [_ workspace _]
  (document-plans/update-workspace workspace))

