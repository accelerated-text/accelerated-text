(ns api.graphql.amr-test
  (:require [api.graphql.core :as graph]
            [api.graphql.queries :as queries]
            [clojure.test :refer [deftest]]))

(deftest ^:integration full-query-test
  (queries/validate-resp (graph/nlg (queries/list-verbclasses))))
