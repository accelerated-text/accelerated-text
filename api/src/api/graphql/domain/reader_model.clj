(ns api.graphql.domain.reader-model
  (:require [api.graphql.domain.dictionary :as dict-domain]
            [api.graphql.translate.reader-model :as rm-translate]
            [clojure.string :as str]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.dictionary :as dict-entity]
            [data.entities.language :as lang-entity]))

(defn reader-model [_ _ _]
  (resolve-as
    {:id    "default"
     :flags (map rm-translate/language->reader-flag (lang-entity/listing))}))

(defn- resolve-as-not-found-reader-flag [id]
  (resolve-as nil {:message (format "Cannot find reader flag with id `%s`." id)}))

(defn reader-flag [_ {:keys [id]} _]
  (if-let [item (lang-entity/fetch id)]
    (resolve-as (rm-translate/language->reader-flag item))
    (resolve-as-not-found-reader-flag id)))

(defn update-reader-flag-usage [_ {:keys [id usage]} _]
  (if-let [item (dict-entity/get-dictionary-item (dict-domain/get-parent-id id))]
    (let [[parent-part phrase-part flag-id] (str/split id #"/")
          flag-key (keyword flag-id)
          phrase-id (format "%s/%s" parent-part phrase-part)
          select-pair (fn [flags] (list flag-key (get flags flag-key)))]
      (->> (dict-domain/update-phrase item phrase-id #(assoc-in % [:flags flag-key] (keyword usage)) false)
           (:flags)
           (select-pair)
           (rm-translate/reader-flag-usage->schema phrase-id)
           (resolve-as)))
    (dict-domain/resolve-as-not-found-dict-item (dict-domain/get-parent-id id))))
