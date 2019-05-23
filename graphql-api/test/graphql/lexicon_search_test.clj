(ns graphql.lexicon-search-test
  (:require [clojure.test :refer :all]
            [graphql.core :as graph]
            [jsonista.core :as json]))

(defn normalize-resp [resp]
  (-> resp (json/write-value-as-string) (json/read-value)))

(deftest ^:integration search-lexicon
  (is (= {"data" {"searchLexicon" {"totalCount" 8
                                   "offset" 0
                                   "limit" 20
                                   "items" [{"key" "good.3"}
                                            {"key" "good.6"}
                                            {"key" "good.8"}
                                            {"key" "good.9"}
                                            {"key" "good.12"}
                                            {"key" "good.16"}
                                            {"key" "good.17"}
                                            {"key" "good.19"}]}}}
         (normalize-resp (graph/nlg {:query "{searchLexicon(query: \"good\"){offset limit totalCount items{key}}}"}))))
  (is (= {"data" {"searchLexicon" {"limit"      20 "totalCount" 8}}}
         (normalize-resp (graph/nlg {:query "{searchLexicon(query: \"good\"){limit totalCount}}"}))))
  (is (= {"data" {"searchLexicon" {"totalCount" 45426, "offset" 0, "limit" 20}}}
         (normalize-resp (graph/nlg {:query "{searchLexicon(query: \"\"){offset limit totalCount}}"})))))
