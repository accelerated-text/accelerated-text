(ns api.nlg.core
  (:require [acc-text.nlg.core :as nlg]
            [acc-text.nlp.utils :as nlp]
            [acc-text.nlg.semantic-graph.utils :refer [get-dictionary-keys]]
            [api.nlg.ref-expr :refer [enable-ref-expr? apply-ref-expressions]]
            [api.nlg.enrich :refer [enable-enrich? enrich]]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [api.utils :as utils]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.amr :refer [find-amrs]]
            [data.entities.dictionary :refer [build-dictionaries default-language]]
            [data.spec.result :as result]
            [data.spec.result.annotation :as annotation]
            [data.spec.result.row :as row]))

(defn add-annotations [{text ::row/text :as row}]
  (assoc row ::row/annotations (mapv (fn [{:keys [idx text]}]
                                       #::annotation{:id   (utils/gen-uuid)
                                                     :idx  idx
                                                     :text text})
                                     (nlp/annotate text))))

(defn ->result-row [{:keys [text language enriched?]}]
  #::row{:id        (utils/gen-uuid)
         :language  language
         :enriched? (true? enriched?)
         :text      (cond->> text (enable-ref-expr?) (apply-ref-expressions language))})

(defn generate-text
  [{:keys [id document-plan data languages] :or {id (utils/gen-uuid) data {} languages [(default-language)]}}]
  (let [semantic-graph (document-plan->semantic-graph document-plan)
        amrs (find-amrs semantic-graph)
        semantic-graphs (cons semantic-graph amrs)
        dictionary-keys (set (concat (vals data) (mapcat get-dictionary-keys semantic-graphs)))
        dictionaries (build-dictionaries dictionary-keys languages)]
    (try
      #::result{:id     id
                :status :ready
                :rows   (transduce
                          (comp
                            (mapcat (fn [language]
                                      (let [context {:amr amrs :data data :dictionary (get dictionaries language [])}]
                                        (cond-> (nlg/generate-text semantic-graph context language)
                                                (and (= "Eng" language) (enable-enrich?)) (enrich data)))))
                            (remove #(str/blank? (:text %)))
                            (map ->result-row)
                            (map add-annotations))
                          conj
                          languages)}
      (catch Exception e
        (log/error (.getMessage e))
        (log/trace (str/join "\n" (.getStackTrace e)))
        #::result{:id            id
                  :status        :error
                  :error-message (or (.getMessage e) "")}))))
