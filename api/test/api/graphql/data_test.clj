(ns api.graphql.data-test
  (:require [api.graphql.core :as graph]
            [api.graphql.queries :as queries]
            [clojure.test :refer [deftest]]))

(deftest ^:integration get-data-test
  (queries/validate-resp (graph/nlg (queries/get-data-file "example-user/books.csv" 0 20))))

(deftest ^:integration list-data-test
  (queries/validate-resp (graph/nlg (queries/list-data-files 0 20 0 20))))
