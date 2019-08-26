(ns graphql.data-test
  (:require [clojure.test :refer :all]
            [graphql.queries :as queries]
            [graphql.core :as graph]))

(deftest ^:integration get-data-test
  (queries/validate-resp (graph/nlg (queries/get-data "example-user" "shoes.csv"))))

(deftest ^:integration list-data-test
  (queries/validate-resp (graph/nlg (queries/list-data "example-user" 20))))
