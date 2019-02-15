(ns lt.tokenmill.nlg.db.config)

(def blockly-table "blockly-workspace")
(def results-table "nlg-results")
(def data-table "data")

(def client-opts
  {:endpoint "http://dynamodb.eu-central-1.amazonaws.com"
   :profile "tm"})
