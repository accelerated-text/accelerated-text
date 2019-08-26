(ns graphql.data-test
  (:require [clojure.test :refer :all]
            [graphql.queries :as queries]
            [graphql.core :as graph]))

(deftest ^:integration get-data-test
  (queries/validate-resp (graph/nlg (queries/get-data-file "example-user" "shoes.csv" 0 20))))

(deftest ^:integration list-data-test
  (queries/validate-resp (graph/nlg (queries/list-data-files "example-user" 0 20))))
