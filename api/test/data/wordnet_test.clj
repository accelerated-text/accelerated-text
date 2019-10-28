(ns data.wordnet-test
  (:require [clojure.test :refer [deftest is]]
            [data.wordnet :as wn]))

(deftest lookup-words-test
  (is (zero? (count (wn/lookup-words ""))))
  (is (pos-int? (count (wn/lookup-words "word")))))
