(ns api.graphql.translate.dictionary
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [data.utils :as utils]
            [data.spec.language :as language]
            [data.entities.language :as lang-entity]))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (lang-entity/list-languages)))
  ([text parent-id default-usage default-flags]
   {:id    (format "%s/%s" parent-id (utils/gen-uuid))
    :text  text
    :flags (assoc default-flags (lang-entity/list-languages) default-usage)}))

(defn build-lang-user-flags [lang]
  (map (fn [{::language/keys [code name enabled?]}]
         {:id    (utils/gen-uuid)
          :usage (if (= name lang) "YES" "NO")
          :flag  {:id           code
                  :name         name
                  :defaultUsage (if enabled? "YES" "NO")}})
       (lang-entity/list-languages)))

(defn dictionary-item->schema [{::dictionary-item/keys [id key category forms language]}]
  {:id           (or id (utils/gen-uuid))
   :name         key
   :partOfSpeech category
   :phrases      (map (fn [form]
                        {:id              (utils/gen-uuid)
                         :text            form
                         :defaultUsage    "YES"
                         :readerFlagUsage (build-lang-user-flags language)})
                      forms)})

(defn schema->dictionary-item [{id :id item-name :name pos :partOfSpeech}]
  #::dictionary-item{:id       id
                     :key      (if (some? pos)
                                 (format "%s_%s" item-name (name pos))
                                 item-name)
                     :category (name pos)
                     :sense    "1"
                     :language "Eng"
                     :forms    [item-name]})
