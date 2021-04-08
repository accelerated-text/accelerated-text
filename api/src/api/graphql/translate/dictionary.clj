(ns api.graphql.translate.dictionary
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [acc-text.nlg.dictionary.item.attr :as dict-item-attr]
            [acc-text.nlg.semantic-graph :as sg]
            [api.graphql.translate.concept :as concept-translate]
            [data.entities.amr :as amr-entity]
            [data.entities.user-group :as user-group]
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

(defn get-concept [attributes]
  (when-let [concept-name (some #(when (= "Concept" (::dict-item-attr/name %))
                                   (::dict-item-attr/value %))
                                attributes)]
    (some #(when (= concept-name (::sg/name %))
             (concept-translate/amr->schema %))
          (amr-entity/list-amrs user-group/DUMMY-USER-GROUP-ID))))

(defn dictionary-item->schema [{::dict-item/keys [id key category forms language definition sense attributes]}]
  {:id           (or id (utils/gen-uuid))
   :name         key
   :partOfSpeech category
   :language     language
   :sense        sense
   :definition   definition
   :phrases      (map (fn [{::dict-item-form/keys [id value default?]}]
                        {:id              id
                         :text            value
                         :defaultUsage    (if default? "YES" "NO")
                         :readerFlagUsage (build-reader-model-user-flags language)})
                      forms)
   :concept      (get-concept attributes)
   :attributes   (map (fn [{::dict-item-attr/keys [id name value]}]
                        {:id    id
                         :name  name
                         :value value})
                      attributes)})

(defn schema->dictionary-item
  [{id :id item-name :name key :key pos :partOfSpeech forms :forms lang :language sense :sense definition :definition attrs :attributes}]
  #::dict-item{:id         (or id (utils/gen-uuid))
               :key        (cond
                             (some? key) key
                             (some? pos) (format "%s_%s" item-name (name pos))
                             :else item-name)
               :category   (if (some? pos) (name pos) "V")
               :sense      (or sense "1")
               :definition (or definition "")
               :language   (if (some? lang) (name lang) "Eng")
               :forms      (map (fn [form]
                                  #::dict-item-form{:id    (utils/gen-uuid)
                                                    :value form})
                                (or (seq forms) [item-name]))
               :attributes (map (fn [{:keys [id name value]}]
                                  #::dict-item-attr{:id    (or id (utils/gen-uuid))
                                                    :name  name
                                                    :value value})
                                attrs)})
