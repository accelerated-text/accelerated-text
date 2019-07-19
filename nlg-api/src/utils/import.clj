(ns utils.import
  (:require [data-access.entities.amr :as amr-entity]
            [ccg-kit.verbnet.core :as verbnet]))

(defn import-verbnet
  []
  (let [data (verbnet/xml->vclass "resources/verbnet/battle.xml")]
    (amr-entity/create-verbclass {:id (:id data)
                                  :thematic-roles (:thematic-roles data)
                                  :frames (:frames data)})))

