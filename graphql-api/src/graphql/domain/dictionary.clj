(ns graphql.domain.dictionary
  (:require [clojure.tools.logging :as log]
            [translate.dictionary :as translate-dict]
            [translate.core :as translate-core]
            [data-access.entities.dictionary :as dict-entity]
            [clojure.string :as str]))


(defn dictionary [_ _ _]
  (->> (dict-entity/list-dictionary)
      (map translate-dict/dictionary-item->schema)
      (translate-core/paginated-response)))

(defn dictionary-item [_ arguments _]
  (log/debugf "Fetching dictionary item with args: %s" arguments)
  (translate-dict/dictionary-item->schema
   (dict-entity/get-dictionary-item (:id arguments))))

(defn create-dictionary-item [_ {:keys [name partOfSpeech phrases]} _]
  (dict-entity/create-dictionary-item {:key name
                                       :name name
                                       :phrases phrases
                                       :partOfSpeech partOfSpeech})
  (translate-dict/dictionary-item->schema
   (dict-entity/get-dictionary-item name)))

(defn delete-dictionary-item [_ {:keys [id]} _]
  (dict-entity/delete-dictionary-item id)
  true)

(defn update-dictionary-item [_ {:keys [id name partOfSpeech]} _]
  (translate-dict/dictionary-item->schema
   (dict-entity/update-dictionary-item {:key id
                                        :name name
                                        :partOfSpeech partOfSpeech})))

(defn create-phrase [_ {:keys [dictionaryItemId text defaultUsage]} _]
  (log/debugf "Creating phrase: %s %s %s" dictionaryItemId text defaultUsage)
  (let [current-item (dict-entity/get-dictionary-item dictionaryItemId)
        default-flags (dict-entity/get-default-flags)
        phrases (cons (dict-entity/text->phrase
                       text
                       dictionaryItemId
                       (keyword defaultUsage)
                       default-flags)
                      (:phrases current-item))]
    (translate-dict/dictionary-item->schema
     (dict-entity/update-dictionary-item (assoc current-item :phrases phrases)))))


(defn update-phrase [id mut-fn translate?]
  (let [[parent-id & _] (str/split id #"/")
        current-item (dict-entity/get-dictionary-item parent-id)
        updated-phrases (map
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

(defn update-phrase-text [_ {:keys [id text]} _]
  (update-phrase
   id
   (fn [item] (assoc item :text text))
   true))


(defn update-phrase-default-usage [_ {:keys [id defaultUsage]} _]
  (update-phrase
   id
   (fn [item] (assoc-in item [:flags :default] (keyword defaultUsage)))
   true))


(defn delete-phrase [_ {:keys [id]} _]
  (let [[parent-id & _] (str/split id #"/")
        current-item (dict-entity/get-dictionary-item parent-id)]
    (translate-dict/dictionary-item->schema
     (dict-entity/update-dictionary-item (assoc current-item :phrases (filter
                                                                       (fn [item] (not= id (:id item)))
                                                                       (:phrases current-item)))))))


(defn reader-flags [_ _ _]
  (translate-dict/reader-flags->schema
   (dict-entity/list-readers)))

(defn reader-flag [_ arguments _]
  (translate-dict/reader-flag->schema
   (dict-entity/get-reader (:id arguments))))

(defn update-reader-flag-usage [_ {:keys [id usage]} _]
  (let [[parent-part phrase-part flag-id] (str/split id #"/")
        flag-key                          (keyword flag-id)
        phrase-id                         (format "%s/%s" parent-part phrase-part)
        select-pair                       (fn [flags] (list flag-key (get flags flag-key)))]
    (->> (update-phrase
          phrase-id
          (fn [item] (assoc-in item [:flags flag-key] (keyword usage)))
          false)
         :flags
         (select-pair)
         (translate-dict/reader-flag-usage->schema phrase-id))))
