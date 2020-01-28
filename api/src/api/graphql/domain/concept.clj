(ns api.graphql.domain.concept
  (:require [api.graphql.translate.concept :as concept-translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.amr :as amr-entity]
            [data.entities.rgl :as rgl-entity]))

(def kinds ["A" "A2" "AP" "AdA" "AdN" "AdNalmost" "AdNat" "AdV" "Adv" "Ant" "CAdv"
            "CN" "CNvery" "Card" "Cl" "ClSlash" "Comp" "Conj" "Det" "Dig" "Digits"
            "Gender" "IAdv" "IComp" "IDet" "IP" "IQuant" "Imp" "ImpForm" "Interj"
            "ListAP" "ListAdv" "ListNP" "ListRS" "ListS" "N" "N2" "N3" "NP" "Num"
            "Number" "Numeral""Ord" "PConj" "PN" "Phr" "Pol" "Predet" "Prep" "Pron"
            "Punct" "QCl" "QS" "Quant" "RCl" "RP" "RS" "S" "SC" "SSlash" "Str" "Sub100"
            "Sub1000" "Subj" "Temp" "Tense" "Text" "Type" "Unit" "Utt" "V" "V2" "V2A"
            "V2Q" "V2S" "V2V" "V3" "VA" "VP" "VPSlash" "VQ" "VS" "VV" "Voc"])

(defn list-concepts [_ _ _]
  (let [concepts (->> (amr-entity/load-all)
                      (concat (rgl-entity/load-all))
                      (map concept-translate/amr->schema)
                      (sort-by :id))]
    (resolve-as
      (merge {:id       "concepts"
              :concepts concepts}
             (reduce-kv (fn [m k v]
                          (assoc m (keyword k) v))
                        {}
                        (select-keys (group-by :kind concepts) (map name kinds)))))))

(defn- resolve-as-not-found-concept [id]
  (resolve-as nil {:message (format "Cannot find concept with id `%s`." id)}))

(defn get-concept [_ {:keys [id]} _]
  (if-let [concept (or (amr-entity/load-single id) (rgl-entity/load-single id))]
    (resolve-as (concept-translate/amr->schema concept))
    (resolve-as-not-found-concept id)))
