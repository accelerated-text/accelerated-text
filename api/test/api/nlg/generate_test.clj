(ns api.nlg.generate-test
  (:require [api.nlg.generate :refer [wrap-to-annotated-text generate-request generation-process]]
            [clojure.test :refer [deftest is]]
            [clojure.walk :refer [postwalk]]))

(deftest ^:integration generate-request-test
  (let [{:keys [status]} (generate-request
                           {:documentPlanId   "f9bc3183-f3e1-41d5-a27f-52a0ee584793"
                            :dataId           "example-user/books.csv"
                            :readerFlagValues {}})]
    (is (= 200 status))))

(deftest ^:integration basic-generation
  (let [documentPlanId "8fa07eda-68d1-480f-a8e5-d39385977ca3"
        dataId "example-user/data-example.csv"
        body (generation-process documentPlanId dataId nil)]
    (println (format "Generation result: %s", body))
    (let [results (:results body)]
      (is (= 1 (count results))))))

(deftest ^:integration basic-generation-ccg
  (let [documentPlanId "8fa07eda-68d1-480f-a8e5-d39385977ca3"
        dataId "example-user/ccg-example.csv"
        body (generation-process documentPlanId dataId nil)]
    (println (format "Generation result: %s", body))
    (let [results (:results body)]
      (is (= 1 (count results))))))

(deftest annotated-text-formatting
  (let [variants ["Nike Air offers watch and contemplate. Premium lacing gives snug fit for everyday wear."
                  "Nike Air 2 offers comfort, value."
                  "Nike Air 90's offers look and style."]
        [r1 r2 r3 :as res] (wrap-to-annotated-text variants)]
    (is (= 3 (count res)))
    (is (= {:type        "ANNOTATED_TEXT"
            :annotations []
            :references  []
            :children    [{:type     "PARAGRAPH"
                           :children [{:type     "SENTENCE"
                                       :children [{:type "WORD", :text "Nike"}
                                                  {:type "WORD", :text "Air"}
                                                  {:type "WORD", :text "offers"}
                                                  {:type "WORD", :text "watch"}
                                                  {:type "WORD", :text "and"}
                                                  {:type "WORD", :text "contemplate"}
                                                  {:type "PUNCTUATION", :text "."}]}
                                      {:type     "SENTENCE"
                                       :children [{:type "WORD", :text "Premium"}
                                                  {:type "WORD", :text "lacing"}
                                                  {:type "WORD", :text "gives"}
                                                  {:type "WORD", :text "snug"}
                                                  {:type "WORD", :text "fit"}
                                                  {:type "WORD", :text "for"}
                                                  {:type "WORD", :text "everyday"}
                                                  {:type "WORD", :text "wear"}
                                                  {:type "PUNCTUATION", :text "."}]}]}]}
           (postwalk #(cond-> % (map? %) (dissoc :id)) r1)))
    (is (= {:type        "ANNOTATED_TEXT"
            :annotations []
            :references  []
            :children    [{:type     "PARAGRAPH",
                           :children [{:type     "SENTENCE",
                                       :children [{:type "WORD" :text "Nike"}
                                                  {:type "WORD" :text "Air"}
                                                  {:type "WORD" :text "2"}
                                                  {:type "WORD" :text "offers"}
                                                  {:type "WORD" :text "comfort"}
                                                  {:type "PUNCTUATION" :text ","}
                                                  {:type "WORD" :text "value"}
                                                  {:type "PUNCTUATION" :text "."}]}]}]}
           (postwalk #(cond-> % (map? %) (dissoc :id)) r2)))
    (is (= {:type        "ANNOTATED_TEXT"
            :annotations []
            :references  []
            :children    [{:type     "PARAGRAPH"
                           :children [{:type     "SENTENCE"
                                       :children [{:type "WORD" :text "Nike"}
                                                  {:type "WORD" :text "Air"}
                                                  {:type "WORD" :text "90's"}
                                                  {:type "WORD" :text "offers"}
                                                  {:type "WORD" :text "look"}
                                                  {:type "WORD" :text "and"}
                                                  {:type "WORD" :text "style"}
                                                  {:type "PUNCTUATION" :text "."}]}]}]}
           (postwalk #(cond-> % (map? %) (dissoc :id)) r3)))))
