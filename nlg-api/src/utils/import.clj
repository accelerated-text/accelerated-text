(ns utils.import
  (:require [data-access.entities.amr :as amr-entity]
            [ccg-kit.verbnet.core :as verbnet]))

(defn import-member
  [member]
  (amr-entity/create-member {:name (:name member)
                             :grouping (when (not= "" (:grouping member))
                                         (:grouping member))}))

(defn import-verbnet
  []
  (let [data (verbnet/xml->vclass "resources/verbnet/battle.xml")
        members (map import-member (:members data))]
    (amr-entity/create-verbclass {:id (:id data)
                                  :members (map :id members)
                                  :thematic-roles (:thematic-roles data)
                                  :frames (:frames data)})))

