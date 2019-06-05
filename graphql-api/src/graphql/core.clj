(ns graphql.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [nlg.lexicon :as lexicon]
            [nlg.org-dictionary :as org-dictionary]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia :refer [execute]]))

(defn get-lexicon [context arguments value]
  (let [result (:body (lexicon/search arguments nil))]
    (update result :items #(map (fn [{:keys [key] :as item}] (assoc item :id key)) %))))

(defn org-dictionary [_ _ _]
  (org-dictionary/org-dictionary))

(defn org-dictionary-item [_ arguments _]
  (org-dictionary/org-dictionary-item arguments))

#_(defn org-dictionary-item [_ _ _]
  {:id "dictionary-item-test-id" :name "dictionary-item-test-name" :usageModels []})

(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers {:Query {:searchLexicon :get-lexicon
                                                :orgDictionary :org-dictionary
                                                :orgDictionaryItem :org-dictionary-item}}})
      (util/attach-resolvers {:get-lexicon get-lexicon
                              :org-dictionary org-dictionary
                              :org-dictionary-item org-dictionary-item})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (execute nlg-schema query variables context))
