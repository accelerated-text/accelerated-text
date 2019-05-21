(ns graphql.core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [nlg.lexicon :as lexicon]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]])
  (:import (clojure.lang BigInt)))

(def transforms
  {:big-int-parser     #(when (= BigInt (type %)) %)
   :big-int-serializer #(when (= BigInt (type %)) %)})

(defn get-lexicon [context arguments value]
  (:body (lexicon/search arguments nil)))

(def nlg-schema
  (-> "schema.edn"
      (io/resource)
      slurp
      edn/read-string
      (util/attach-scalar-transformers transforms)
      (util/attach-resolvers {:get-lexicon get-lexicon})
      schema/compile))

(defn nlg [request]
  (log/info "The request is: %s" request)
  (execute nlg-schema "{\n  hero {\n    id\n    name\n  }\n}" nil nil))
