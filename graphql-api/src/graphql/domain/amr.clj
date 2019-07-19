(ns graphql.domain.amr
  (:require [data-access.entities.amr :as amr-entity]
            [translate.amr :as amr-translate]
            [translate.core :as translate-core]
            [clojure.tools.logging :as log]))

(defn list-verbclasses [_ _ _]
  {:id "concepts"
   :concepts (map amr-translate/verbclass->schema
                  (amr-entity/list-verbclasses))})

(defn get-verbclass [_ {:keys [id]} _]
  (amr-translate/verbclass->schema
   (amr-entity/get-verbclass id)))

