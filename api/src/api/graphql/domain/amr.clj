(ns api.graphql.domain.amr
  (:require [api.graphql.translate.amr :as amr-translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.amr :as amr-entity]))

(defn list-verbclasses [_ _ _]
  (resolve-as
    {:id       "concepts"
     :concepts (map amr-translate/verbclass->schema
                    (amr-entity/list-verbclasses))}))

(defn get-verbclass [_ {:keys [id]} _]
  (if-let [verbclass (amr-entity/get-verbclass id)]
    (resolve-as (amr-translate/verbclass->schema verbclass))
    (resolve-as nil {:message (format "Cannot find concept with id `%s`." id)})))
