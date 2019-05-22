(ns graphql.core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [nlg.lexicon :as lexicon]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]))

(defn get-lexicon [context arguments value]
  (log/infof "'get-lexicon' arguments -> '%s'" arguments)
  (let [result (lexicon/search arguments nil)]
    (log/infof "Lexicon search results -> '%s'" result)
    (:body result)))

(def nlg-schema
  (-> "schema.edn"
      (io/resource)
      slurp
      edn/read-string
      (util/attach-resolvers {:get-lexicon get-lexicon})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/info "The request is: %s" request)
  (execute nlg-schema query variables context))
