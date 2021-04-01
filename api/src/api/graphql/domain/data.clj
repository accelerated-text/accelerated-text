(ns api.graphql.domain.data
  (:require [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.data-files :as data-files]
            [clojure.tools.logging :as log]))

(defn- resolve-as-not-found-file [id]
  (resolve-as nil {:message (format "Cannot find data file with id `%s`." id)}))

(defn get-data-file [{:keys [auth-info]}
                     {:keys [id recordOffset recordLimit]
                      :or   {recordOffset 0 recordLimit 20}} _]
  (if-let [data-file (data-files/fetch id recordOffset recordLimit (:group-id auth-info))]
    (resolve-as data-file)
    (resolve-as-not-found-file id)))

(defn get-relevant-samples [{:keys [auth-info]} {:keys [id method recordOffset recordLimit]
                               :or {recordOffset 0 recordLimit 20 method "relevant"}} _]
  (let [data-fn (case method
                      "relevant" data-files/fetch-most-relevant
                      "first"    data-files/fetch)]
    (if-let [data-file (data-fn id recordOffset recordLimit (:group-id auth-info))]
      (resolve-as data-file)
      (resolve-as-not-found-file id))))

(defn list-data-files [{:keys [auth-info]}
                       {:keys [offset limit recordOffset recordLimit]
                        :or   {offset 0 limit 20 recordOffset 0 recordLimit 20}}
                       _]
  (resolve-as (data-files/listing (:group-id auth-info) offset limit recordOffset recordLimit)))

(defn create-data-file [{:keys [auth-info]} request _]
  (resolve-as {:id (data-files/store! request (:group-id auth-info))}))
