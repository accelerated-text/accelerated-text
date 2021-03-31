(ns api.graphql.domain.dictionary
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [api.graphql.translate.core :as translate-core]
            [api.graphql.translate.dictionary :as translate-dict]
            [api.graphql.translate.reader-model :as rm-translate]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.dictionary :as dict-entity]))

(defn resolve-as-not-found-dict-item [id]
  (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." id)}))

(defn dictionary [_ _ _]
  (->> (dict-entity/list-dictionary-items)
       (map translate-dict/dictionary-item->schema)
       (sort-by #(-> [(:partOfSpeech %) (:name %)]))
       (translate-core/paginated-response)
       (resolve-as)))

(defn dictionary-item [_ {id :id :as args} _]
  (log/debugf "Fetching dictionary item with args: %s" args)
  (if-let [item (dict-entity/get-dictionary-item id)]
    (resolve-as (translate-dict/dictionary-item->schema item))
    (resolve-as-not-found-dict-item id)))

(defn create-dictionary-item [_ args _]
  (-> (translate-dict/schema->dictionary-item args)
      (dict-entity/create-dictionary-item)
      (translate-dict/dictionary-item->schema)
      (resolve-as)))

(defn delete-dictionary-item [_ {:keys [id]} _]
  (dict-entity/delete-dictionary-item id)
  (resolve-as true))

(defn update-dictionary-item [_ {id :id :as args} _]
  (if-let [item (dict-entity/update-dictionary-item (translate-dict/schema->dictionary-item args))]
    (resolve-as (translate-dict/dictionary-item->schema item))
    (resolve-as-not-found-dict-item id)))

(defn create-phrase [_ {:keys [dictionaryItemId text defaultUsage]} _]
  (if-let [item (dict-entity/get-dictionary-item dictionaryItemId)]
    (let [phrase (translate-dict/text->phrase text dictionaryItemId (= :YES defaultUsage))]
      (-> item
          (update ::dict-item/forms #(conj % phrase))
          (dict-entity/update-dictionary-item)
          (translate-dict/dictionary-item->schema)
          (resolve-as)))
    (resolve-as-not-found-dict-item dictionaryItemId)))

(defn update-phrase [{lang ::dict-item/language :as item} id mut-fn translate?]
  (let [translate-fn (if (true? translate?) #(rm-translate/phrase->schema % lang) identity)]
    (->> (update item ::dict-item/forms #(map (fn [phrase] (cond-> phrase (= id (::dict-item-form/id phrase)) (mut-fn))) %))
         (dict-entity/update-dictionary-item)
         (::dict-item/forms)
         (filter #(= id (::dict-item-form/id %)))
         (first)
         (translate-fn))))

(defn get-parent-id [id]
  (first (str/split id #"/")))

(defn resolve-as-not-found-phrase [id]
  (resolve-as nil {:message (format "Cannot find dictionary item having form with id `%s`." id)}))

(defn update-phrase-text [_ {:keys [id text]} _]
  (if-let [item (dict-entity/get-parent #::dict-item-form{:id id})]
    (resolve-as (update-phrase item id #(assoc % ::dict-item-form/value text) true))
    (resolve-as-not-found-phrase id)))

(defn update-phrase-default-usage [_ {:keys [id defaultUsage]} _]
  (if-let [item (dict-entity/get-parent #::dict-item-form{:id id})]
    (-> item
        (update-phrase id #(assoc % ::dict-item-form/default? (= :YES defaultUsage)) true)
        (resolve-as))
    (resolve-as-not-found-phrase id)))

(defn delete-phrase [_ {:keys [id]} _]
  (if-let [item (dict-entity/get-parent #::dict-item-form{:id id})]
    (-> item
        (update ::dict-item/forms #(remove (fn [form] (= id (::dict-item-form/id form))) %))
        (dict-entity/update-dictionary-item)
        (translate-dict/dictionary-item->schema)
        (resolve-as))
    (resolve-as-not-found-phrase id)))
