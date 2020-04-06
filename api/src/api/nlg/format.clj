(ns api.nlg.format
  (:require [api.utils :as utils]
            [data.spec.result :as result]
            [data.spec.result.row :as row]
            [data.spec.result.annotation :as annotation]))

(def default-format-type "annotated-text")

(defn show-flags? []
  (Boolean/valueOf ^String (or (System/getenv "SHOW_FLAGS") "true")))

(def enriched-flag {:type "FLAG"
                    :id   "ENRICHED"
                    :text "📙"})

(defn get-flag [lang]
  {:type "FLAG"
   :id   lang
   :text (case lang
           "Eng" "🇬🇧"
           "Ger" "🇩🇪"
           "Est" "🇪🇪"
           "Lav" "🇱🇻"
           "Rus" "🇷🇺"
           "🏳️")})

(defn get-flags [{::row/keys [language enriched?]}]
  (cond-> [(get-flag language)]
          (true? enriched?) (conj enriched-flag)))

(defn ->annotated-text-format [{rows ::result/rows}]
  (map (fn [{annotations ::row/annotations :as row}]
         {:type        "ANNOTATED_TEXT"
          :id          (utils/gen-uuid)
          :annotations []
          :references  []
          :children    [{:type     "PARAGRAPH"
                         :id       (utils/gen-uuid)
                         :children [{:type     "SENTENCE"
                                     :id       (utils/gen-uuid)
                                     :children (concat
                                                 (when (show-flags?)
                                                   (get-flags row))
                                                 (map (fn [{::annotation/keys [id text]}]
                                                        {:type "WORD"
                                                         :id   id
                                                         :text text})
                                                      annotations))}]}]}) ;; TODO
       rows))

(defn ->raw-format [{::result/keys [rows]}]
  (map ::row/text rows))

(defn use-format [format-type result]
  (case format-type
    "raw" (->raw-format result)
    "annotated-text" (->annotated-text-format result)))

(defn with-default-format [result]
  (use-format default-format-type result))
