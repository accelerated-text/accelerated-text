(ns api.end-to-end-test
  (:require [api.db-fixtures :as fixtures]
            [api.test-utils :refer [generate-text]]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each fixtures/clean-db)


