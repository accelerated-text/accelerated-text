(ns data.datomic.ops
  (:require [datomic.client.api :as d]))

(def movie-schema
  [{:db/ident :movie/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The title of the movie"}

   {:db/ident :movie/genre
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The genre of the movie"}

   {:db/ident :movie/release-year
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "The year the movie was released in theaters"}])

(def first-movies
  [{:movie/title "The Goonies"
    :movie/genre "action/adventure"
    :movie/release-year 1985}
   {:movie/title "Commando"
    :movie/genre "action/adventure"
    :movie/release-year 1985}
   {:movie/title "Repo Man"
    :movie/genre "punk dystopia"
    :movie/release-year 1984}])

(comment
  (def cfg {:server-type :peer-server
            :access-key "myaccesskey"
            :secret "mysecret"
            :endpoint "localhost:8998"
            :validate-hostnames false})

  (def client (d/client cfg))

  (def conn (d/connect client {:db-name "hello"}))

  (d/transact conn {:tx-data movie-schema})

  (d/transact conn {:tx-data first-movies})

  (def db (d/db conn))

  (def all-movies-q '[:find ?e
                      :where [?e :movie/title]])

  (d/q all-movies-q db)

  (d/q '[:find ?movie-title ?release-year
         :keys title year
         :where [?title :movie/title ?movie-title]
         [?title :movie/title ?movie-title]
         [?year :movie/release-year ?release-year]] db)


  (d/q '[:find (pull ?e [*])
         :where [?e :movie/title]
         [?e :movie/release-year 1985] ] db))
