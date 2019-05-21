(ns graphql.core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]))

(defn get-hero [context arguments value]
  (let [{:keys [episode]} arguments]
    (if (= episode :NEWHOPE)
      {:id "1000"
       :name "Luke"
       :home_planet "Tatooine"
       :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]}
      {:id "2000"
       :name "Lando Calrissian"
       :home_planet "Socorro"
       :appears_in ["EMPIRE" "JEDI"]})))

(defn get-droid [context arguments value]
  (if (= (:id arguments) "2001")
    {:id 2001
     :name "droid 2k1"
     :appears_in ["EMPIRE" "NEWHOPE"]
     :primary_functions ["zapping" "hacking" "beeping"]}
    {:id 666
     :name "devil droid"
     :appears_in ["EMPIRE"]
     :primary_functions ["zapping" "hacking" "killing"]}))

(def star-wars-schema
  (-> "schema.edn"
      (io/resource)
      slurp
      edn/read-string
      (attach-resolvers {:get-hero get-hero
                         :get-droid get-droid
                         ;:friends (constantly {})
                         :get-human get-hero})
      schema/compile))

(defn nlg [request]
  (log/info "The request is: %s" request)
  (execute star-wars-schema "{\n  hero {\n    id\n    name\n  }\n}" nil nil))
