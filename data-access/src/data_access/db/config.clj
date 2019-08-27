(ns data-access.db.config)

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

(def amr-member-table {:table-name "amr-members"
                       :table-key :id})

(def amr-verbclass-table {:table-name "amr-verbclass"
                          :table-key :id})

(defn dynamodb-endpoint []
  (or (System/getenv "DYNAMODB_ENDPOINT") "http://dynamodb.eu-central-1.amazonaws.com/"))

(defn client-opts []
  (let [access-key-id (System/getenv "AWS_ACCESS_KEY_ID")
        secret-key (System/getenv "AWS_SECRET_ACCESS_KEY")]
    (if (and (some? access-key-id) (some? secret-key))
      {:endpoint (dynamodb-endpoint)
       :access-key access-key-id
       :secret-key secret-key}
      {:endpoint (dynamodb-endpoint)
       :profile "tm"})))

(defn s3-endpoint [] (System/getenv "S3_ENDPOINT"))
