(ns api.graphql.domain.concept
  (:require [api.graphql.translate.concept :as concept-translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.amr :as amr-entity]
            [data.entities.rgl :as rgl-entity]))

(defn list-concepts [_ _ _]
  (resolve-as
    (merge
      {:id        "concepts"
       :concepts  []
       :amr       (->> (amr-entity/list-amrs)
                       (map concept-translate/amr->schema)
                       (sort-by :id))
       :rgl       (->> (rgl-entity/read-library)
                       (map concept-translate/amr->schema))
       :paradigms (->> (amr-entity/list-rgls)
                       (map concept-translate/amr->schema)
                       (sort-by :id))}
      (->> (rgl-entity/read-paradigms)
           (group-by :module)
           (reduce-kv (fn [m k v]
                        (assoc m (keyword k) (map concept-translate/amr->schema v)))
                      {})))))

(defn- resolve-as-not-found-concept [id]
  (resolve-as nil {:message (format "Cannot find concept with id `%s`." id)}))

(defn get-concept [_ {:keys [id]} _]
  (if-let [concept (or (amr-entity/get-amr id) (rgl-entity/get-rgl id))]
    (resolve-as (concept-translate/amr->schema concept))
    (resolve-as-not-found-concept id)))
