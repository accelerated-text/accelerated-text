(ns graphql.domain.amr
  (:require [data-access.entities.amr :as amr-entity]
            [translate.amr :as amr-translate]
            [translate.core :as translate-core]
            [clojure.tools.logging :as log]))

(defn list-verbclasses [_ _ _]
  (->> (amr-entity/list-verbclasses)
       (map amr-translate/verbclass->schema)
       (translate-core/paginated-response)))

(defn get-verbclass [_ {:keys [id]} _]
  (amr-translate/verbclass->schema
   (amr-entity/get-verbclass id)))

(defn list-members [_ _ _]
  (->> (amr-entity/list-members)
       (map amr-translate/member->schema)
       (translate-core/paginated-response)))

(defn get-member [_ {:keys [id]} _]
  (amr-translate/member->schema
   (amr-entity/get-member id)))

(defn get-members [_ _ value]
  (->> (amr-entity/get-members (:members value))
       :amr-members
       (map amr-translate/member->schema)))

