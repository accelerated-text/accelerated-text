(ns api.nlg.text-annotation-test
  (:require [api.nlg.generate :refer [wrap-to-annotated-text]]
            [clojure.test :refer [deftest is]]
            [clojure.walk :refer [postwalk]]))

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
