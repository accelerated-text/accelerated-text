(ns api.graphql.domain.language
  (:require [api.graphql.domain.dictionary :as dict-domain]
            [api.graphql.translate.reader-model :as rm-translate]
            [clojure.string :as str]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.dictionary :as dict-entity]
            [data.entities.reader-model :as reader-model-entity]
            [data.spec.reader-model :as reader-model]))

(defn language-model [{:keys [auth-info]} _ _]
  (resolve-as
   {:id    "default"
    :flags (map rm-translate/reader-model->reader-flag (reader-model-entity/available-languages (:group-id auth-info)))}))

(defn- resolve-as-not-found-language [id]
  (resolve-as nil {:message (format "Cannot find language with code `%s`." id)}))

(defn language [{:keys [auth-info]} {:keys [id]} _]
  (if-let [item (some #(when (= (::reader-model/code %) (name id)) %) (reader-model-entity/available-languages (:group-id auth-info)))]
    (resolve-as (rm-translate/reader-model->reader-flag item))
    (resolve-as-not-found-language (name id))))

(defn add-language [{:keys [auth-info]} args _]
  (-> (rm-translate/reader-flag->reader-model :language args)
      (reader-model-entity/update! (:group-id auth-info))
      (rm-translate/reader-model->reader-flag)))

(defn delete-language [{:keys [auth-info]} {:keys [id]} _]
  (reader-model-entity/delete! (name id) (:group-id auth-info))
  (resolve-as true))

(defn update-language-usage [{:keys [auth-info]} {:keys [id usage]} _]
  (if-let [item (dict-entity/get-dictionary-item (dict-domain/get-parent-id id))]
    (let [[parent-part phrase-part flag-id] (str/split id #"/")
          flag-key    (keyword flag-id)
          phrase-id   (format "%s/%s" parent-part phrase-part)
          select-pair (fn [flags] (list flag-key (get flags flag-key)))]
      (->> (dict-domain/update-phrase item phrase-id (:group-id auth-info) #(assoc-in % [:flags flag-key] (keyword usage)) false)
           (:flags)
           (select-pair)
           (rm-translate/reader-flag-usage->schema phrase-id)
           (resolve-as)))
    (dict-domain/resolve-as-not-found-dict-item (dict-domain/get-parent-id id))))
