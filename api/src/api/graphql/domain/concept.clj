(ns api.graphql.domain.concept
  (:require [acc-text.nlg.gf.operations :as ops]
            [api.graphql.translate.concept :as concept-translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.amr :as amr-entity]))

(defn list-concepts [_ _ _]
  (resolve-as
    (merge
      {:id         "concepts"
       :concepts   []
       :amr        (->> (amr-entity/list-amrs)
                        (map concept-translate/amr->schema)
                        (sort-by :id))
       :rgl        (map concept-translate/operation->schema ops/syntax)
       :structural (map concept-translate/operation->schema ops/structural-words)
       :grammar    (map concept-translate/operation->schema ops/grammar)
       :paradigms  (->> (amr-entity/list-rgls)
                        (map concept-translate/amr->schema)
                        (sort-by :id))}
      (->> ops/paradigms
           (group-by :module)
           (reduce-kv (fn [m k v]
                        (assoc m (keyword k) (map concept-translate/operation->schema v)))
                      {})))))

(defn- resolve-as-not-found-concept [id]
  (resolve-as nil {:message (format "Cannot find concept with id `%s`." id)}))

(defn get-amr-concept [id]
  (some->> id (amr-entity/get-amr) (concept-translate/amr->schema)))

(defn get-rgl-concept [id]
  (some->> id (get ops/operation-map) (concept-translate/operation->schema)))

(defn get-concept [_ {:keys [id]} _]
  (if-let [concept (or (get-amr-concept id) (get-rgl-concept id))]
    (resolve-as concept)
    (resolve-as-not-found-concept id)))
