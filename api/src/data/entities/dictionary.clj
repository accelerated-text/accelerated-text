(ns data.entities.dictionary
  (:require [clojure.string :as str]
            [data.db.dynamo-ops :as ops]
            [data.utils :as utils]))

(defn list-readers []
  (ops/list! (ops/db-access :reader-flag) 100))

(defn get-default-flags []
  (->> (list-readers)
       (map (fn [r] {(keyword (:id r)) :DONT_CARE}))
       (into {})))

(defn get-reader [key]
  (ops/read! (ops/db-access :reader-flag) key))

(defn list-dictionary []
  (ops/list! (ops/db-access :dictionary-combined) 100))

(defn get-dictionary-item [key]
  (when-not (str/blank? key)
    (ops/read! (ops/db-access :dictionary-combined) key)))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (get-default-flags)))
  ([text parent-id default-usage default-flags]
   {:id    (format "%s/%s" parent-id (utils/gen-uuid))
    :text  text
    :flags (assoc default-flags :default default-usage)}))

(defn create-dictionary-item [{:keys [key name phrases partOfSpeech]}]
  (when-not (str/blank? name)
    (ops/write! (ops/db-access :dictionary-combined) key {:name         name
                                                          :partOfSpeech partOfSpeech
                                                          :phrases      (map #(text->phrase % key :YES) phrases)})))

(defn delete-dictionary-item [key]
  (ops/delete! (ops/db-access :dictionary-combined) key))

(defn update-dictionary-item [item]
  (ops/update! (ops/db-access :dictionary-combined) (:key item) (dissoc item :key)))
