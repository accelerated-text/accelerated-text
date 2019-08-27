(ns data-access.db.wordnet-test
  (:require [clojure.test :refer :all]
            [data-access.wordnet.core :as wn]))

(deftest lookup-words-test
  (is (zero? (count (wn/lookup-words ""))))
  (is (pos-int? (count (wn/lookup-words "word")))))
