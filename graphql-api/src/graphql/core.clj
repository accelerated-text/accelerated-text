(ns graphql.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [nlg.lexicon :as lexicon]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]))

(defn get-lexicon [context arguments value]
  (let [result (:body (lexicon/search arguments nil))]
    (update result :items #(map (fn [{:keys [key] :as item}] (assoc item :id key)) %))))

(def nlg-schema
  (-> "schema.edn"
      (io/resource)
      slurp
      edn/read-string
      (util/attach-resolvers {:get-lexicon get-lexicon})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (execute nlg-schema query variables context))
