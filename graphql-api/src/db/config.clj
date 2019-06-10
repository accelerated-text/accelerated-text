(ns db.config)

(def blockly-table "blockly-workspace")
(def results-table "nlg-results")
(def data-table "data")
(def lexicon-table "lexicon")
(def data-bucket "accelerated-text-data-files")
(def dictionary-table "dictionary")
(def phrase-usage-model-table "phrase-usage-model")
(def reader-flag-usage-table "reader-flag-usage")
(def reader-flag-table "reader-flag")
(def phrase-table "phrase")

(defn client-opts []
  {:endpoint (or (System/getenv "DYNAMODB_ENDPOINT") "http://localhost:8000")
   :profile "tm"})
