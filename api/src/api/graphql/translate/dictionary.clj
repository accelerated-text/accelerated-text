(ns api.graphql.translate.dictionary
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [data.entities.reader-model :as reader-model]
            [data.utils :as utils]))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (reader-model/available-languages)))
  ([text _ _ _]
   #::dict-item-form{:id (utils/gen-uuid) :value text}))

(defn build-reader-model-user-flags [lang]
  (map (fn [{:data.spec.reader-model/keys [code name enabled?]}]
         {:id    (utils/gen-uuid)
          :usage (if (= code lang) "YES" "NO")
          :flag  {:id           code
                  :name         name
                  :defaultUsage (if enabled? "YES" "NO")}})
       (reader-model/available-languages)))

(defn dictionary-item->schema [{::dict-item/keys [id key category forms language]}]
  {:id           (or id (utils/gen-uuid))
   :name         key
   :partOfSpeech category
   :phrases      (map (fn [{::dict-item-form/keys [id value default?]}]
                        {:id              id
                         :text            value
                         :defaultUsage    (if default? "YES" "NO")
                         :readerFlagUsage (build-reader-model-user-flags language)})
                      forms)})

(defn schema->dictionary-item [{id :id item-name :name pos :partOfSpeech}]
  #::dict-item{:id       (or id (utils/gen-uuid))
               :key      (if (some? pos)
                           (format "%s_%s" item-name (name pos))
                           item-name)
               :category (name pos)
               :sense    "1"
               :language "Eng"
               :forms    [#::dict-item-form{:id (utils/gen-uuid) :value item-name}]})
