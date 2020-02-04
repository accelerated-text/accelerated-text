(ns api.graphql.domain.concept
  (:require [api.graphql.translate.concept :as concept-translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.amr :as amr-entity]))

(defn list-concepts [_ _ _]
  (resolve-as
    {:id       "concepts"
     :concepts (->> (amr-entity/list-amrs)
                    (map concept-translate/amr->schema)
                    (sort-by :id))}))

(defn add-concept [_ {:keys [id content]} _]
  (resolve-as
    (->> (amr-entity/read-amr id content)
         (amr-entity/write-amr)
         (concept-translate/amr->schema))))

(defn delete-concept [_ {id :id} _]
  (amr-entity/delete-amr id)
  (resolve-as true))

(defn- resolve-as-not-found-concept [id]
  (resolve-as nil {:message (format "Cannot find concept with id `%s`." id)}))

(defn get-concept [_ {:keys [id]} _]
  (if-let [amr (amr-entity/get-amr id)]
    (resolve-as (concept-translate/amr->schema amr))
    (resolve-as-not-found-concept id)))
