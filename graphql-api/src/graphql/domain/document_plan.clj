(ns graphql.domain.document-plan
  (:require [cheshire.core :as json]
            [data-access.entities.document-plan :as dp]
            [data-access.utils :refer [ts-to-string]]))

(defn- document-plan-ts->string [document-plan]
  (-> document-plan
      (update :createdAt ts-to-string)
      (update :updatedAt ts-to-string)))

(defn get-document-plan [_ {:keys [id]} _]
  (document-plan-ts->string
    (dp/get-document-plan id)))

(defn delete-document-plan [_ {:keys [id]} _]
  (dp/delete-document-plan id)
  true)

(defn list-document-plans [_ {:keys [offset limit] :or {offset 0 limit 20}} _]
  (let [items (dp/list-document-plans)]
    {:items      (->> items (drop offset) (take limit) (map document-plan-ts->string))
     :limit      limit
     :offset     offset
     :totalCount (count items)}))

(defn add-document-plan [_ {:keys [id] :as args} _]
  (document-plan-ts->string
    (dp/add-document-plan id (update args :blocks #(json/parse-string % true)))))

(defn update-document-plan [_ {:keys [id] :as args} _]
  (document-plan-ts->string
    (dp/update-document-plan id (update args :blocks #(json/parse-string % true)))))
