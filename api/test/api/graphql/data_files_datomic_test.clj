(ns api.graphql.data-files-datomic-test
  (:require [api.datomic-fixtures :as df]
            [clojure.test :refer [deftest is testing]]
            [data.entities.data-files :as data-files]
            [mount.core :as mount]
            [clojure.string :as str])
  (:import (java.util UUID)))

(deftest ^:integration writing-an-reading
  (let [db-name (str (UUID/randomUUID))
        conn (df/scratch-conn db-name)
        _ (df/migrate conn)]
    (-> (mount/swap-states {#'api.config/conf {:start (fn [] {:db-implementation  :datomic
                                                              :db-name            db-name
                                                              :db-uri             (str "datomic:mem://" db-name)
                                                              :validate-hostnames false})}})
        (mount/only #{#'api.config/conf
                      #'data.entities.data-files/data-files-db
                      #'data.datomic.impl/conn})
        (mount/start)))
  (testing "Read books.csv headers"
    (let [data-file-id (data-files/store!
                         {:filename "example-user/books.csv"
                          :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
          result (data-files/read-data-file-content "example-user" data-file-id)
          headers (-> result (str/split-lines) (first) (str/split #",") (set))]
      (is (= #{"pageCount" "publishedDate" "ratingsCount" "authors" "maturityRating"
               "id" "categories" "averageRating" "thumbnail" "subtitle"
               "title" "publisher" "language" "isbn-13"} headers))))
  (mount/stop))
