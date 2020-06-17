(ns api.graphql.translate.dictionary
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [data.utils :as utils]
            [data.spec.language :as language]
            [data.entities.language :as lang-entity]
            [clojure.tools.logging :as log]))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (lang-entity/list-languages)))
  ([text _ _ _]
   #::dict-item-form{:id (utils/gen-uuid) :value text}))

(defn build-lang-user-flags [lang]
  (map (fn [{::language/keys [code name enabled?]}]
         {:id    (utils/gen-uuid)
          :usage (if (= name lang) "YES" "NO")
          :flag  {:id           code
                  :name         name
                  :defaultUsage (if enabled? "YES" "NO")}})
       (lang-entity/list-languages)))

(defn dictionary-item->schema [{::dict-item/keys [id key category forms language]}]
  {:id           (or id (utils/gen-uuid))
   :name         key
   :partOfSpeech category
   :phrases      (map (fn [{::dict-item-form/keys [id value]}]
                        {:id              id
                         :text            value
                         :defaultUsage    "YES"
                         :readerFlagUsage (build-lang-user-flags language)})
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
