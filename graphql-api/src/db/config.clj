(ns db.config)

(def blockly-table "blockly-workspace")
(def results-table "nlg-results")
(def data-table "data")
(def lexicon-table "lexicon")
(def data-bucket "accelerated-text-data-files")

(def client-opts
  {:endpoint "http://dynamodb.eu-central-1.amazonaws.com"
   :profile "tm"})
