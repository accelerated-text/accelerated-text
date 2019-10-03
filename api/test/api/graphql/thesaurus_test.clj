(ns api.graphql.thesaurus-test
  (:require [api.graphql.core :as graph]
            [api.graphql.queries :as queries]
            [clojure.test :refer [deftest]]))

(deftest ^:integration search-thesaurus-test
  (queries/validate-resp (graph/nlg (queries/search-thesaurus "word" "NN")))
  (queries/validate-resp (graph/nlg (queries/search-thesaurus "run" "VB"))))

(deftest ^:integration synonyms-test
  (queries/validate-resp (graph/nlg (queries/synonyms "NN-word")))
  (queries/validate-resp (graph/nlg (queries/synonyms "VB-run"))))
