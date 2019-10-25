(ns data.ddb.config)

(def blockly-table {:table-name "blockly-workspace"
                    :table-key  :id})

(def results-table {:table-name "nlg-results"
                    :table-key  :key})

(def data-table {:table-name "data"
                 :table-key  :key})

(def lexicon-table {:table-name "lexicon"
                    :table-key  :key})

(def dictionary-combined-table {:table-name "dictionary-combined"
                                :table-key  :key})

(def dictionary-table {:table-name "dictionary"
                       :table-key  :id})

(def phrase-usage-model-table {:table-name "phrase-usage-model"
                               :table-key  :prim-kvs})

(def reader-flag-usage-table {:table-name "reader-flag-usage"
                              :table-key  :prim-kvs})

(def reader-flag-table {:table-name "reader-flag"
                        :table-key  :id})

(def phrase-table {:table-name "phrase"
                   :table-key  :id})

(def amr-member-table {:table-name "amr-members"
                       :table-key  :id})

(def amr-verbclass-table {:table-name "amr-verbclass"
                          :table-key  :id})

(def data-files-table {:table-name "data-files"
                       :table-key  :id})

(defn dynamodb-endpoint []
  (or (System/getenv "DYNAMODB_ENDPOINT") "http://localhost:8000"))

(defn client-opts []
  {:endpoint (dynamodb-endpoint)})
