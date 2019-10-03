(ns api.graphql.domain.dictionary
  (:require [api.graphql.translate.core :as translate-core]
            [api.graphql.translate.dictionary :as translate-dict]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.dictionary :as dict-entity]))

(defn dictionary [_ _ _]
  (resolve-as (->> (dict-entity/list-dictionary)
                   (map translate-dict/dictionary-item->schema)
                   (translate-core/paginated-response))))

(defn dictionary-item [_ {id :id :as args} _]
  (log/debugf "Fetching dictionary item with args: %s" args)
  (if-let [item (dict-entity/get-dictionary-item id)]
    (resolve-as (translate-dict/dictionary-item->schema item))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." id)})))

(defn ref-dictionary-item [_ _ {:keys [dictionaryItem]}]
  (log/debugf "Fetching ref dictionary item: %s" dictionaryItem)
  (if-let [item (dict-entity/get-dictionary-item dictionaryItem)]
    (resolve-as (translate-dict/dictionary-item->schema item))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." dictionaryItem)})))

(defn create-dictionary-item [_ {item-name :name pos :partOfSpeech phrases :phrases} _]
  (let [key (cond->> item-name (some? pos) (str (name pos) "-"))]
    (dict-entity/create-dictionary-item {:key          key
                                         :name         item-name
                                         :phrases      phrases
                                         :partOfSpeech pos})
    (resolve-as (translate-dict/dictionary-item->schema
                  (dict-entity/get-dictionary-item key)))))

(defn delete-dictionary-item [_ {:keys [id]} _]
  (dict-entity/delete-dictionary-item id)
  (resolve-as true))

(defn update-dictionary-item [_ {id :id :as args} _]
  (if-let [item (dict-entity/update-dictionary-item args)]
    (resolve-as (translate-dict/dictionary-item->schema item))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." id)})))

(defn create-phrase [_ {:keys [dictionaryItemId text defaultUsage]} _]
  (log/debugf "Creating phrase: %s %s %s" dictionaryItemId text defaultUsage)
  (if-let [current-item (dict-entity/get-dictionary-item dictionaryItemId)]
    (let [default-flags (dict-entity/get-default-flags)
          phrases (cons (dict-entity/text->phrase
                          text
                          dictionaryItemId
                          (keyword defaultUsage)
                          default-flags)
                        (:phrases current-item))]
      (resolve-as (translate-dict/dictionary-item->schema
                    (dict-entity/update-dictionary-item (assoc current-item :phrases phrases)))))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." dictionaryItemId)})))

(defn- update-phrase [current-item id mut-fn translate?]
  (let [updated-phrases (map
                          (fn [item]
                            (if (= id (:id item))
                              (mut-fn item)
                              item))
                          (:phrases current-item))
        translate-fn (if translate?
                       translate-dict/phrase->schema
                       (fn [item] item))]
    (dict-entity/update-dictionary-item (assoc current-item :phrases updated-phrases))
    (-> (filter #(= id (:id %)) updated-phrases)
        (first)
        (translate-fn))))

(defn- get-parent-id [id]
  (first (str/split id #"/")))

(defn update-phrase-text [_ {:keys [id text]} _]
  (if-let [current-item (dict-entity/get-dictionary-item (get-parent-id id))]
    (resolve-as (update-phrase
                  current-item
                  id
                  (fn [item] (assoc item :text text))
                  true))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." (get-parent-id id))})))

(defn update-phrase-default-usage [_ {:keys [id defaultUsage]} _]
  (if-let [current-item (dict-entity/get-dictionary-item (get-parent-id id))]
    (resolve-as (update-phrase
                  current-item
                  id
                  (fn [item] (assoc-in item [:flags :default] (keyword defaultUsage)))
                  true))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." (get-parent-id id))})))

(defn delete-phrase [_ {:keys [id]} _]
  (if-let [current-item (dict-entity/get-dictionary-item (get-parent-id id))]
    (resolve-as (translate-dict/dictionary-item->schema
                  (dict-entity/update-dictionary-item (assoc current-item :phrases (filter
                                                                                     (fn [item] (not= id (:id item)))
                                                                                     (:phrases current-item))))))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." (get-parent-id id))})))

(defn reader-flags [_ _ _]
  (resolve-as (translate-dict/reader-flags->schema
                (dict-entity/list-readers))))

(defn reader-flag [_ {id :id} _]
  (if-let [item (dict-entity/get-reader id)]
    (resolve-as (translate-dict/reader-flag->schema item))
    (resolve-as nil {:message (format "Cannot find reader flag with id `%s`." id)})))

(defn update-reader-flag-usage [_ {:keys [id usage]} _]
  (if-let [current-item (dict-entity/get-dictionary-item (get-parent-id id))]
    (let [[parent-part phrase-part flag-id] (str/split id #"/")
          flag-key (keyword flag-id)
          phrase-id (format "%s/%s" parent-part phrase-part)
          select-pair (fn [flags] (list flag-key (get flags flag-key)))]
      (resolve-as (->> (update-phrase
                         current-item
                         phrase-id
                         (fn [item] (assoc-in item [:flags flag-key] (keyword usage)))
                         false)
                       :flags
                       (select-pair)
                       (translate-dict/reader-flag-usage->schema phrase-id))))
    (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." (get-parent-id id))})))
