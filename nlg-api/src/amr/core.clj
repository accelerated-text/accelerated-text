(ns amr.core
  (:require [data-access.entities.amr :as amr-entity]))

(defn get-rule
  [id]
  (case id
    (amr-entity/get-verbclass id)))


