(ns api.graphql.translate.reader-model
  (:require [clojure.tools.logging :as log]
            [data.spec.language :as lang]))

(defn reader-flag-usage->schema [id [k v]]
  (log/debugf "Got: k=%s v=%s" k v)
  {:usage v
   :id    (format "%s/%s" id (name k))
   :flag  {:id   (name k)
           :name (name k)}})

(defn phrase->schema [{:keys [id text flags] :as phrase}]
  (log/tracef "Phrase: %s" phrase)
  {:id              id
   :text            text
   :defaultUsage    (:default flags)
   :readerFlagUsage (map (partial reader-flag-usage->schema id)
                         (dissoc flags :default))})

(defn language->reader-flag [{::lang/keys [code name enabled?]}]
  {:id           code
   :name         name
   :defaultUsage (if (true? enabled?) "YES" "NO")})
