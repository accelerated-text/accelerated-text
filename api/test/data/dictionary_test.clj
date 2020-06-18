(ns data.dictionary-test
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.dictionary :as dictionary]
            [data.utils :as utils]))

(def test-item-eng #::dict-item{:id       "place_Eng"
                                :key      "place_1_N"
                                :sense    "1"
                                :category "N"
                                :language "Eng"
                                :forms    [#::dict-item-form{:id (utils/gen-uuid) :value "place"}
                                           #::dict-item-form{:id (utils/gen-uuid) :value "places"}]})

(def test-item-ger #::dict-item{:id       "place_Ger"
                                :key      "place_1_N"
                                :sense    "1"
                                :category "N"
                                :language "Ger"
                                :forms    [#::dict-item-form{:id (utils/gen-uuid) :value "platz"}
                                           #::dict-item-form{:id (utils/gen-uuid) :value "plätze"}]})

(defn prepare-environment [f]
  (doseq [item [test-item-eng test-item-ger]]
    (dictionary/create-dictionary-item item))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration list-dictionary-items
  (is (= #{test-item-eng test-item-ger} (into #{} (dictionary/list-dictionary-items)))))

(deftest ^:integration search-dictionary-items
  (is (= #{} (into #{} (dictionary/scan-dictionary #{} #{}))))
  (is (= #{} (into #{} (dictionary/scan-dictionary #{} #{}))))
  (is (= #{test-item-eng test-item-ger} (into #{} (dictionary/scan-dictionary #{"place_1_N"} #{"Eng" "Ger"}))))
  (is (= #{test-item-eng} (into #{} (dictionary/scan-dictionary #{"place_1_N"} #{"Eng"}))))
  (is (= #{test-item-ger} (into #{} (dictionary/scan-dictionary #{"place_1_N"} #{"Ger"})))))
