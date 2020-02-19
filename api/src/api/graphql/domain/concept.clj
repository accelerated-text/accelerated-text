(ns api.graphql.domain.concept
  (:require [api.graphql.translate.concept :as concept-translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.amr :as amr-entity]
            [data.entities.rgl :as rgl-entity]))

(defn list-concepts [_ _ _]
  (let [amr-concepts (sort-by :id (map concept-translate/amr->schema (amr-entity/list-amrs)))
        paradigms (sort-by :id (map concept-translate/amr->schema (amr-entity/list-rgls)))
        rgl-library (map concept-translate/amr->schema (rgl-entity/read-library))
        rgl-paradigms-raw (rgl-entity/read-paradigms)
        rgl-paradigms (map concept-translate/amr->schema rgl-paradigms-raw)]
    (resolve-as
      (merge
        (->> rgl-paradigms-raw
             (group-by :module)
             (reduce-kv (fn [m k v]
                          (assoc m (keyword k) (map concept-translate/amr->schema v)))
                        {}))
        {:id        "concepts"
         :concepts  (concat amr-concepts paradigms rgl-library rgl-paradigms)
         :amr       amr-concepts
         :rgl       rgl-library
         :paradigms paradigms}))))

(defn- resolve-as-not-found-concept [id]
  (resolve-as nil {:message (format "Cannot find concept with id `%s`." id)}))

(defn get-concept [_ {:keys [id]} _]
  (if-let [concept (or (amr-entity/get-amr id) (rgl-entity/get-rgl id))]
    (resolve-as (concept-translate/amr->schema concept))
    (resolve-as-not-found-concept id)))
