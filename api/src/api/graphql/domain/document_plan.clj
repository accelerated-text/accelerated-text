(ns api.graphql.domain.document-plan
  (:require [api.graphql.translate.document-plan :as translate-dp]
            [clojure.string :as str]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.document-plan :as dp]))

(defn- resolve-as-not-found-document-plan [id]
  (resolve-as nil {:message (format "Cannot find document plan `%s`." id)}))

(defn- resolve-as-id-not-provided []
  (resolve-as nil {:message "Either document plan `id` or `name` must be provided."}))

(defn delete-document-plan [_ {:keys [id]} _]
  (dp/delete-document-plan id)
  (resolve-as true))

(defn list-document-plans [{:keys [auth-info]} {:keys [offset limit kind] :or {offset 0 limit 100}} _]
  (let [items (if (some? kind) (dp/list-document-plans kind (:group-id auth-info)) (dp/list-document-plans (:group-id auth-info)))]
    (resolve-as {:items      (->> items
                                  (drop offset)
                                  (take limit)
                                  (map translate-dp/dp->schema)
                                  (vec))
                 :kind       kind
                 :limit      limit
                 :offset     offset
                 :totalCount (count items)})))

(defn add-document-plan [{:keys [auth-info]} args _]
  (-> (translate-dp/schema->dp args)
      (dp/add-document-plan (:group-id auth-info))
      (translate-dp/dp->schema)
      (resolve-as)))

(defn get-document-plan [_ args _]
  (let [id (when-not (str/blank? (:id args)) (:id args))
        name (when-not (str/blank? (:name args)) (:name args))
        document-plan (cond
                        (some? id) (dp/get-document-plan id)
                        (some? name) (some #(when (= name (:name %)) %) (dp/list-document-plans)))]
    (cond
      (some? document-plan) (resolve-as (translate-dp/dp->schema document-plan))
      (or id name) (resolve-as-not-found-document-plan (or id name))
      :else (resolve-as-id-not-provided))))

(defn update-document-plan [_ {:keys [id] :as args} _]
  (if-let [document-plan (dp/update-document-plan id (translate-dp/schema->dp args))]
    (resolve-as (translate-dp/dp->schema document-plan))
    (resolve-as-not-found-document-plan id)))
