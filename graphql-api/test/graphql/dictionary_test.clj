(ns graphql.dictionary-test
  (:require [clojure.test :refer :all]
            [graphql.core :as graph]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [clojure.set :as set]))

(defn normalize-resp [resp]
  (-> resp (json/write-value-as-string) (json/read-value)))

(defn dict-item-name
  [[item phrases]]
  (:name item))

(deftest ^:integration list-dictionary
  (let [result (normalize-resp (graph/nlg {:query "{dictionary{items{name} totalCount}}"}))
        expected {"data" {"dictionary" {"totalCount" 3 "items" [{"name" "provides"} {"name" "see"} {"name" "redesigned"}]}}}]
    (is (= expected result))))

(deftest ^:integration list-dictionary-phrases
  (let [resp (graph/nlg {:query "{dictionary{items{name phrases{text}}}}"})
        expected (list (list {:name "provides"} {:phrases '({:text"gives"} {:text "offers"} {:text "provides"})})
                       (list {:name "redesigned"} {:phrases '({:text "revamped"} {:text "new"} {:text "redesigned"})})
                       (list {:name "see"} {:phrases '({:text "gaze"} {:text "contemplate"} {:text "check out"} {:text "watch"} {:text "see"} {:text "examine"} {:text "look"})}))
        result (->> (:data resp)
                    :dictionary
                    :items
                    (partition 2)
                    (sort-by dict-item-name))]
    (log/tracef "Expected:\t %s\n" expected)
    (log/tracef "Result:\t %s\n" result)
    (is (= expected result))))

(deftest ^:integration get-dictionary-item
  (let [resp (graph/nlg {:query "{dictionaryItem(id: \"see\"){name partOfSpeech phrases{text}}}"})
        result (->> (:data resp)
                    :dictionaryItem)]
    (log/debugf "Response: %s" resp)
    (is (= "see" (:name result)))
    (is (= :VB  (:partOfSpeech result)))
    (is (= #{{:text "look"}
             {:text "contemplate"}
             {:text "examine"}
             {:text "gaze"}
             {:text "watch"}
             {:text "check out"}
             {:text "see"}}
           (set (:phrases result))))))
