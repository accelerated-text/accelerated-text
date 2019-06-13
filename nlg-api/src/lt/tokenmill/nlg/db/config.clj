(ns lt.tokenmill.nlg.db.config)

(def blockly-table "blockly-workspace")
(def results-table {:table-name "nlg-results"
                    :table-key :key})
(def data-table {:table-name "data"
                 :table-key :key})
(def lexicon-table {:table-name "lexicon"
                    :table-key :key})
(def data-bucket "accelerated-text-data-files")
(def grammar-bucket "ccg-grammar")


(def dictionary-combined-table {:table-name "dictionary-combined"
                                :table-key :key})

(def dictionary-table {:table-name "dictionary"
                       :table-key :id})
(def phrase-usage-model-table {:table-name "phrase-usage-model"
                               :table-key :prim-kvs})
(def reader-flag-usage-table {:table-name "reader-flag-usage"
                              :table-key :prim-kvs})
(def reader-flag-table {:table-name "reader-flag"
                        :table-key :id})
(def phrase-table {:table-name "phrase"
                   :table-key :id})

(defn client-opts []
  {:endpoint (or (System/getenv "DYNAMODB_ENDPOINT") "http://dynamodb.eu-central-1.amazonaws.com/")
   :profile "tm"})

(defn s3-endpoint [] (System/getenv "S3_ENDPOINT"))
