(ns graphql.domain.data
  (:require [cheshire.core :as json]
            [clojure.set :as set]
            [data-access.data.core :as data]))

(defn list-data [_ {:keys [user limit]} _]
  {:data (map #(set/rename-keys % {:field-names :fieldNames})
              (data/list-data user limit))})

(defn get-data [_ {:keys [user file]} _]
  (update (data/get-data user file) :data json/encode))
