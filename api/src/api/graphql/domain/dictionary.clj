(ns api.graphql.domain.dictionary
  (:require [api.graphql.translate.core :as translate-core]
            [api.graphql.translate.dictionary :as translate-dict]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.entities.dictionary :as dict-entity]))

(defn dictionary [_ _ _]
  (->> (dict-entity/list-dictionary)
       (map translate-dict/dictionary-item->schema)
       (sort-by :name)
       (translate-core/paginated-response)
       (resolve-as)))

(defn- resolve-as-not-found-dict-item [id]
  (resolve-as nil {:message (format "Cannot find dictionary item with id `%s`." id)}))

(defn dictionary-item [_ {id :id :as args} _]
  (log/debugf "Fetching dictionary item with args: %s" args)
  (if-let [item (dict-entity/get-dictionary-item id)]
    (resolve-as (translate-dict/dictionary-item->schema item))
    (resolve-as-not-found-dict-item id)))

(defn create-dictionary-item [_ {item-name :name pos :partOfSpeech phrases :phrases} _]
  (-> {:key          (cond->> item-name (some? pos) (str (name pos) "-"))
       :name         item-name
       :phrases      phrases
       :partOfSpeech pos}
      (dict-entity/create-dictionary-item)
      (translate-dict/dictionary-item->schema)
      (resolve-as)))

(defn delete-dictionary-item [_ {:keys [id]} _]
  (dict-entity/delete-dictionary-item id)
  (resolve-as true))

(defn update-dictionary-item [_ {id :id :as args} _]
  (if-let [item (dict-entity/update-dictionary-item args)]
    (resolve-as (translate-dict/dictionary-item->schema item))
    (resolve-as-not-found-dict-item id)))

(defn create-phrase [_ {:keys [dictionaryItemId text defaultUsage]} _]
  (log/debugf "Creating phrase: %s %s %s" dictionaryItemId text defaultUsage)
  (if-let [item (dict-entity/get-dictionary-item dictionaryItemId)]
    (let [phrase (dict-entity/text->phrase text dictionaryItemId (keyword defaultUsage))]
      (-> item
          (update :phrases (partial cons phrase))
          (dict-entity/update-dictionary-item)
          (translate-dict/dictionary-item->schema)
          (resolve-as)))
    (resolve-as-not-found-dict-item dictionaryItemId)))

(defn- update-phrase [item id mut-fn translate?]
  (let [translate-fn (if (true? translate?) translate-dict/phrase->schema identity)]
    (->> (update item :phrases #(map (fn [phrase] (cond-> phrase (= id (:id phrase)) (mut-fn))) %))
         (dict-entity/update-dictionary-item)
         (:phrases)
         (filter #(= id (:id %)))
         (first)
         (translate-fn))))

(defn- get-parent-id [id]
  (first (str/split id #"/")))

(defn update-phrase-text [_ {:keys [id text]} _]
  (if-let [item (dict-entity/get-dictionary-item (get-parent-id id))]
    (-> item
        (update-phrase id #(assoc % :text text) true)
        (resolve-as))
    (resolve-as-not-found-dict-item (get-parent-id id))))

(defn update-phrase-default-usage [_ {:keys [id defaultUsage]} _]
  (if-let [item (dict-entity/get-dictionary-item (get-parent-id id))]
    (-> item
        (update-phrase id #(assoc-in % [:flags :default] (keyword defaultUsage)) true)
        (resolve-as))
    (resolve-as-not-found-dict-item (get-parent-id id))))

(defn delete-phrase [_ {:keys [id]} _]
  (if-let [item (dict-entity/get-dictionary-item (get-parent-id id))]
    (-> item
        (update :phrases #(remove (fn [phrase] (= id (:id phrase)))))
        (dict-entity/update-dictionary-item)
        (translate-dict/dictionary-item->schema)
        (resolve-as))
    (resolve-as-not-found-dict-item (get-parent-id id))))

(defn reader-flags [_ _ _]
  (-> (dict-entity/list-readers)
      (translate-dict/reader-flags->schema)
      (resolve-as)))

(defn- resolve-as-not-found-reader-flag [id]
  (resolve-as nil {:message (format "Cannot find reader flag with id `%s`." id)}))

(defn reader-flag [_ {:keys [id]} _]
  (if-let [item (dict-entity/get-reader id)]
    (resolve-as (translate-dict/reader-flag->schema item))
    (resolve-as-not-found-reader-flag id)))

(defn update-reader-flag-usage [_ {:keys [id usage]} _]
  (if-let [item (dict-entity/get-dictionary-item (get-parent-id id))]
    (let [[parent-part phrase-part flag-id] (str/split id #"/")
          flag-key (keyword flag-id)
          phrase-id (format "%s/%s" parent-part phrase-part)
          select-pair (fn [flags] (list flag-key (get flags flag-key)))]
      (->> (update-phrase item phrase-id #(assoc-in % [:flags flag-key] (keyword usage)) false)
           (:flags)
           (select-pair)
           (translate-dict/reader-flag-usage->schema phrase-id)
           (resolve-as)))
    (resolve-as-not-found-dict-item (get-parent-id id))))
