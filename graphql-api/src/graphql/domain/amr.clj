(ns graphql.domain.amr
  (:require [data-access.entities.amr :as amr-entity]))

(defn list-verbclasses [_ _ _])

(defn get-verbclass [_ {:keys [id]} _])

(defn list-members [_ _ _])

(defn get-member [_ {:keys [id] _}])

