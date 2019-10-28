(ns data.entities.amr
  (:require [data.db :as db]
            [api.config :refer [conf]]
            [mount.core :refer [defstate]]))

(defstate verbclass-db :start (db/db-access :verbclass conf))

(def see
  {:id                 "see"
   :dictionary-item-id "see"
   :thematic-roles     (list {:type "Agent"}
                             {:type "co-Agent"})
   :frames             (list {:examples (list "Harry sees Sally.")
                              :syntax   (list
                                          {:pos :NP :value "Agent"}
                                          {:pos :VERB}
                                          {:pos :NP :value "co-Agent"})})})

(def provide
  {:id                 "provide"
   :dictionary-item-id "provide"
   :thematic-roles     (list {:type "Agent"}
                             {:type "co-Agent"})
   :frames             (list {:examples (list "Nike provides comfort.")
                              :syntax   (list
                                          {:pos :NP :value "Agent"}
                                          {:pos :VERB}
                                          {:pos :NP :value "co-Agent"})})})

(def author
  {:id                 "author"
   :dictionary-item-id "written"
   :thematic-roles     (list {:type "Agent"}
                             {:type "co-Agent"}
                             {:type "Theme" :restrictors '({:type  :determiner
                                                            :value "the"})})
   :frames             (list {:examples (list "X is the author of Y")
                              :syntax   (list
                                          {:pos :NP :value "Agent" :restrictors '({:type  :count
                                                                                   :value :singular})}
                                          {:pos :LEX :value "is"}
                                          {:pos :LEX :value "the author of"}
                                          {:pos :NP :value "co-Agent"})}

                             {:examples (list "X are authors of Y")
                              :syntax   (list {:pos :NP :value "Agent" :restrictors '({:type  :count
                                                                                       :value :plural})}
                                              {:pos :LEX :value "are"}
                                              {:pos :LEX :value "authors of"}
                                              {:pos :NP :value "co-Agent"})}

                             {:examples (list "Y is written by Y")
                              :syntax   (list {:pos :NP :value "co-Agent"}
                                              {:pos :LEX :value "is"}
                                              {:pos :VERB}
                                              {:pos :PREP :value "by"}
                                              {:pos :NP :value "Agent"})})})

(def rules
  {:provide provide
   :see     see
   :author  author})

(defn list-all [] (map (fn [[_ v]] v) rules))

(defn list-verbclasses []
  (concat
    (db/list! verbclass-db 100)
    (list-all)))

(defn get-verbclass [k]
  (if (contains? rules (keyword k))
    (get rules (keyword k))
    (db/read! verbclass-db k)))

(defn create-verbclass
  [{:keys [id _ _ _] :as req}]
  (db/write! verbclass-db id req))
