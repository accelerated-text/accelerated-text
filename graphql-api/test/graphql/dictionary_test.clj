(ns graphql.dictionary-test
  (:require [clojure.test :refer :all]
            [graphql.core :as graph]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [clojure.set :as set]))

(defn normalize-resp [resp]
  (-> resp (json/write-value-as-string) (json/read-value)))

(defn exists-pair?
  [col [name-part phrases-part]]
  (-> (filter (fn [pair] ;; We cannot deconstruct OrderedMap
                (let [[k1 p1] (first pair)
                      [k2 p2] (second pair)]
                  (log/debugf "%s vs %s" (pr-str p2) phrases-part)
                  (log/spyf "Matching result: %s" (and (= p1 (get name-part k1))
                                                       (= (set p2) (set (get phrases-part k2))))))
                )
              (flatten col))
      (empty?)
      (not)))

(defn exists-item?
  [col item]
  (->> (filter (fn [d] (= d item)) col)
      (empty?)
      (not)))

(deftest ^:integration list-dictionary
  (let [result (normalize-resp (graph/nlg {:query "{dictionary{items{name} totalCount}}"}))
        expected {"data" {"dictionary" {"totalCount" 2 "items" [{"name" "provide"} {"name" "see"}]}}}]
    (is (= expected result))))

(deftest ^:integration list-dictionary-phrases
  (let [resp (graph/nlg {:query "{dictionary{items{name phrases{text}}}}"})
        result (->> (:data resp)
                    :dictionary
                    :items
                    (partition 2))]
    (log/debugf "Result:\t %s\n" (pr-str result))
    ;; (is (exists-pair? result (list {:name "provides"} {:phrases '({:text"gives"} {:text "offers"} {:text "provides"})})))
    ;; ;; (is (exists-pair? result (list {:name "redesigned"} {:phrases '({:text "revamped"} {:text "new"} {:text "redesigned"})})))
    ;; (is (exists-pair? result (list {:name "see"} {:phrases '({:text "gaze"} {:text "contemplate"} {:text "check out"} {:text "watch"} {:text "see"} {:text "examine"} {:text "look"})})))
    ))

(deftest ^:integration get-dictionary-item
  (let [resp (graph/nlg {:query "{dictionaryItem(id: \"provide\"){name partOfSpeech phrases{text}}}"})
        result (->> (:data resp)
                    :dictionaryItem)]
    (is (= "provide" (:name result)))
    (is (= :VB  (:partOfSpeech result)))
    (is (= #{{:text "offers"}
             {:text "gives"}
             {:text "provides"}}
           (set (:phrases result))))))

(deftest ^:integration get-reader-flags
  (let [resp (graph/nlg {:query "{readerFlags{flags{name}}}"})
        result (->> (:data resp)
                    :readerFlags
                    :flags)]
    (is (exists-item? result {:name "junior"}))
    (is (exists-item? result {:name "senior"}))))
