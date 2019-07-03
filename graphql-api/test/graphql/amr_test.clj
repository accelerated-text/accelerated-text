(ns graphql.amr-test
  (:require [clojure.test :refer :all]
            [graphql.core :as graph]
            [clojure.tools.logging :as log]
            [graphql.queries :as queries]))


(deftest ^:integration full-query-test
  (queries/validate-resp (graph/nlg (queries/list-verbclasses))))
