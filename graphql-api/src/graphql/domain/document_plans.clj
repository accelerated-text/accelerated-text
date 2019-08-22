(ns graphql.domain.document-plans
  (:require [data-access.entities.document-plans :as document-plans]))

(defn get-workspace [_ {:keys [key]} _]
  (document-plans/get-workspace key))

(defn delete-workspace [_ {:keys [key]} _]
  (document-plans/delete-workspace key))

(defn write-workspace [_ {:keys [key workspace]} _]
  (document-plans/write-workspace key workspace))

(defn list-workspaces [_ {:keys [limit]} _]
  (document-plans/list-workspaces limit))

(defn add-workspace [_ {:keys [key workspace]} _]
  (document-plans/add-workspace key workspace))

(defn update-workspace [_ {:keys [key workspace]} _]
  (document-plans/update-workspace key workspace))
