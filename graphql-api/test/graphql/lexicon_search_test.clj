(ns graphql.lexicon-search-test
  (:require [clojure.test :refer :all]
            [graphql.core :as graph]
            [jsonista.core :as json]))

(defn normalize-resp [resp]
  (-> resp (json/write-value-as-string) (json/read-value)))

;; (deftest ^:integration search-lexicon
;;   (is (= {"data" {"searchLexicon" {"totalCount" 2,
;;                                    "offset"     0,
;;                                    "limit"      20,
;;                                    "items"      [{"key" "good.1", "synonyms" ["good" "amazing" "superb" "peachy"]}
;;                                                  {"key" "good.2", "synonyms" ["good" "complete"]}]}}}
;;          (normalize-resp (graph/nlg {:query "{searchLexicon(query: \"good\"){offset limit totalCount items{key synonyms}}}"}))))
;;   (is (= {"data" {"searchLexicon" {"limit" 20 "totalCount" 2}}}
;;          (normalize-resp (graph/nlg {:query "{searchLexicon(query: \"good\"){limit totalCount}}"}))))
;;   (is (= {"data" {"searchLexicon" {"totalCount" 2, "offset" 0, "limit" 20}}}
;;          (normalize-resp (graph/nlg {:query "{searchLexicon(query: \"\"){offset limit totalCount}}"})))))
