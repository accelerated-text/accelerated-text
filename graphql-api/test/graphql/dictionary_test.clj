(ns graphql.dictionary-test
  (:require [clojure.test :refer :all]
            [graphql.core :as graph]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [clojure.set :as set]
            [data-access.entities.dictionary :as dict-entity]
            [graphql.queries :as queries]))

(defn prepare-environment [f]
  (dict-entity/create-dictionary-item {:key "test-phrase"
                                       :name "test-phrase"
                                       :partOfSpeech "VB"
                                       :phrases ["t1" "t2" "t3"]})
  (f)
  (dict-entity/delete-dictionary-item "test-phrase"))

(defn normalize-resp [resp]
  (-> resp (json/write-value-as-string) (json/read-value)))

(defn exists-pair?
  [col [name-part phrases-part]]
  (-> (filter (fn [pair] ;; We cannot deconstruct OrderedMap
                (let [[k1 p1] (first pair)
                      [k2 p2] (second pair)]
                  (and (= p1 (get name-part k1))
                       (= (set p2) (set (get phrases-part k2))))))
              (flatten col))
      (empty?)
      (not)))

(defn exists-item?
  [col item]
  (->> (filter (fn [d] (= d item)) col)
      (empty?)
      (not)))

(deftest ^:integration full-query-test
  (queries/validate-resp (graph/nlg {:query "{dictionary{items{name partOfSpeech phrases{id text defaultUsage readerFlagUsage{id usage flag{id name}}}}}}"})))

(deftest ^:integration list-dictionary-phrases
  (let [resp (graph/nlg {:query "{dictionary{items{name phrases{text}}}}"})
        result (->> (:data resp)
                    :dictionary
                    :items)]
    (log/debugf "Result:\t %s\n" (pr-str result))
    (is (exists-pair? result (list {:name "test-phrase"} {:phrases '({:text "t1"} {:text "t2"} {:text "t3"})})))))

(deftest ^:integration get-dictionary-item
  (let [resp (graph/nlg {:query "{dictionaryItem(id: \"test-phrase\"){name partOfSpeech phrases{text}}}"})
        result (->> (:data resp)
                    :dictionaryItem)]
    (queries/validate-resp resp)
    (log/debugf "Resp: %s" resp)
    (is (= "test-phrase" (:name result)))
    (is (= :VB  (:partOfSpeech result)))
    (is (= #{{:text "t1"}
             {:text "t2"}
             {:text "t3"}}
           (set (:phrases result))))))

(deftest ^:integration ^:mutation mutation-scenario
  (testing "create dict item"
      (queries/validate-resp (graph/nlg (queries/create-dict-item "test-phrase2" "VB")))
      (let [resp (graph/nlg (queries/get-dict-item "test-phrase2"))
            result (->> (:data resp)
                        :dictionaryItem)]
        (log/debugf "Resp: %s" resp)
        (is (= "test-phrase2" (:name result)))))
  
  (testing "add phrase"
    (let [resp (graph/nlg (queries/create-phrase "test-phrase2" "t1" "YES"))
          phrases (-> (:data resp)
                      :createPhrase
                      :phrases)

          target-id (-> (filter #(= "t1" (:text %)) phrases)
                        (first)
                        :id)]
      (log/debugf "Resp: %s" resp)
      (log/debugf "We've created phrase with ID: %s" target-id)
      (is (not (nil? target-id)))
      (queries/validate-resp (graph/nlg (queries/update-phrase target-id "t2")))
      (queries/validate-resp (graph/nlg (queries/update-phrase-default-usage target-id "NO")))
      (queries/validate-resp (graph/nlg (queries/update-reader-flag-usage (format "%s/%s" target-id "senior") "YES")))))

  (testing "cleanup"
    (queries/validate-resp (graph/nlg (queries/delete-dict-item "test-phrase2")))))

(use-fixtures :once prepare-environment)
