(ns api.graphql.domain.concept
  (:require [api.graphql.translate.concept :as concept-translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.amr :as amr-entity]
            [data.entities.rgl :as rgl-entity]
            [data.entities.paradigms :as paradigms]))

(defn list-concepts [_ _ _]
  (let [amr-concepts (sort-by :id (map concept-translate/amr->schema (amr-entity/list-amrs)))
        paradigms (sort-by :id (map concept-translate/amr->schema (amr-entity/list-rgls)))
        paradigms-eng (map concept-translate/amr->schema (paradigms/load-all-eng))
        paradigms-ger (map concept-translate/amr->schema (paradigms/load-all-ger))
        rgl-concepts (map concept-translate/amr->schema (rgl-entity/load-all))]
    (resolve-as
      {:id           "concepts"
       :concepts     (concat amr-concepts rgl-concepts)
       :amr          amr-concepts
       :rgl          rgl-concepts
       :paradigms    paradigms
       :paradigmsEng paradigms-eng
       :paradigmsGer paradigms-ger})))

(defn- resolve-as-not-found-concept [id]
  (resolve-as nil {:message (format "Cannot find concept with id `%s`." id)}))

(defn get-concept [_ {:keys [id]} _]
  (if-let [concept (or (amr-entity/get-amr id) (rgl-entity/load-single id) (paradigms/load-single id))]
    (resolve-as (concept-translate/amr->schema concept))
    (resolve-as-not-found-concept id)))
