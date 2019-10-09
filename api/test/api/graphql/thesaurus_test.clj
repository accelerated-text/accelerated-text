(ns api.graphql.thesaurus-test
  (:require [api.test-utils :refer [q]]
            [clojure.test :refer [deftest is testing]]))

(deftest ^:integration search-thesaurus-test
  (let [query "{searchThesaurus(query:\"%s\" partOfSpeech:%s){words{id partOfSpeech text concept{id label}} offset limit totalCount}}"]
    (testing "Noun search"
      (let [{{{{:keys [limit offset words totalCount]} :searchThesaurus} :data errors :errors} :body}
            (q "/_graphql" :post {:query (format query "word" "NN")})]
        (is (nil? errors))
        (is (pos-int? limit))
        (is (zero? offset))
        (is (seq words))
        (is (pos-int? totalCount))))
    (testing "Verb search"
      (let [{{{{:keys [limit offset words totalCount]} :searchThesaurus} :data errors :errors} :body}
            (q "/_graphql" :post {:query (format query "run" "VB")})]
        (is (nil? errors))
        (is (pos-int? limit))
        (is (zero? offset))
        (is (seq words))
        (is (pos-int? totalCount))))
    (testing "Non existing word search"
      (let [{{{{:keys [limit offset words totalCount]} :searchThesaurus} :data errors :errors} :body}
            (q "/_graphql" :post {:query (format query "abcdtest" "VB")})]
        (is (nil? errors))
        (is (zero? limit))
        (is (zero? offset))
        (is (nil? (seq words)))
        (is (zero? totalCount))))))

(deftest ^:integration synonyms-test
  (let [query "{synonyms(wordId:\"%s\"){rootWord{id partOfSpeech text concept{id label}} synonyms{id partOfSpeech text concept{id label}}}}"]
    (testing "Nouns"
      (let [{{{{synonyms :synonyms {:keys [id partOfSpeech text]} :rootWord} :synonyms} :data errors :errors} :body}
            (q "/_graphql" :post {:query (format query "NN-word")})]
        (is (nil? errors))
        (is (seq synonyms))
        (is (= id "NN-word"))
        (is (= partOfSpeech "NN"))
        (is (= text "word"))))
    (testing "Verbs"
      (let [{{{{synonyms :synonyms {:keys [id partOfSpeech text]} :rootWord} :synonyms} :data errors :errors} :body}
            (q "/_graphql" :post {:query (format query "VB-run")})]
        (is (nil? errors))
        (is (seq synonyms))
        (is (= id "VB-run"))
        (is (= partOfSpeech "VB"))
        (is (= text "run"))))
    (testing "Non existing words"
      (let [{{{{synonyms :synonyms {:keys [id partOfSpeech text]} :rootWord} :synonyms} :data errors :errors} :body}
            (q "/_graphql" :post {:query (format query "NN-abcdtest")})]
        (is (nil? errors))
        (is (nil? (seq synonyms)))
        (is (= id "NN-abcdtest"))
        (is (= partOfSpeech "NN"))
        (is (= text "abcdtest"))))))
