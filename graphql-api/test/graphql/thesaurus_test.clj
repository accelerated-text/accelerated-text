(ns graphql.thesaurus-test
  (:require [clojure.test :refer :all]
            [graphql.queries :as queries]
            [graphql.core :as graph]))

(deftest ^:integration search-thesaurus-test
  (queries/validate-resp (graph/nlg (queries/search-thesaurus "word" "NN"))))

(deftest ^:integration synonyms-test
  (queries/validate-resp (graph/nlg (queries/synonyms "NN-word"))))
