(ns api.nlg.format
  (:require [api.utils :as utils]
            [clojure.string :as str]
            [data.spec.result :as result]
            [data.spec.result.row :as row]
            [data.spec.result.annotation :as annotation]))

(def default-format-type "annotated-text-shallow")

(defn show-flags? []
  (Boolean/valueOf ^String (or (System/getenv "SHOW_FLAGS") "true")))

(def error-flag {:type "FLAG"
                 :id   "ERROR"
                 :text "\uD83D\uDED1"})

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
           "Spa" "🇪🇸"
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

(defn ->annotated-text-shallow-format [{rows ::result/rows}]
  (map (fn [{text ::row/text :as row}]
         {:type        "ANNOTATED_TEXT"
          :id          (utils/gen-uuid)
          :annotations []
          :references  []
          :children    [{:type "PARAGRAPH"
                         :id   (utils/gen-uuid)
                         :text (str/trim (str/join " " [(when (show-flags?)
                                                          (str/join " " (map :text (get-flags row))))
                                                        text]))}]})
       rows))

(defn ->error [{::result/keys [error-message]}]
  (cond-> []
          (and
            (not (str/blank? error-message))
            (not (str/includes? error-message "java.lang.NullPointerException"))
            (re-matches #"^(?!tmp).*$" error-message))
          (conj {:type     "ERROR"
                 :id       (utils/gen-uuid)
                 :children [error-flag {:type "MESSAGE"
                                        :id   (utils/gen-uuid)
                                        :text error-message}]})))

(defn ->raw-format [{::result/keys [rows]}]
  (map ::row/text rows))

(defn use-format [format-type result]
  (case format-type
    "raw" (->raw-format result)
    "annotated-text" (->annotated-text-format result)
    "annotated-text-shallow" (->annotated-text-shallow-format result)
    "error" (->error result)))

(defn with-default-format [result]
  (use-format default-format-type result))
