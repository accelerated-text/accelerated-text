(ns api.nlg.parser-test
  (:require [api.nlg.parser :as parser]
            [clojure.test :refer [deftest is]]))

(def tiny-dp
  {:segments
         [{:children
                 [{:child  {:name "title" :type "Cell"}
                   :name   "good"
                   :type   "Dictionary-item-modifier"
                   :itemId "NN-good"}]
           :type "Segment"}]
   :type "Document-plan"})

(def book-dp {:type "Document-plan"
              :segments
                    [{:type     "Segment"
                      :children [{:type "Quote" :text "Book on sale"}]}
                     {:children
                            [{:roles          [{:children [{:name   "good"
                                                            :type   "Dictionary-item-modifier"
                                                            :child  {:name "authors" :type "Cell"}
                                                            :itemId "good"}]
                                                :name     "agent"}
                                               {:children [{:name "title" :type "Cell"}]
                                                :name     "co-agent"}
                                               {:children [] :name "theme"}]
                              :dictionaryItem {:name   "written"
                                               :type   "Dictionary-item"
                                               :itemId "written"}
                              :type           "AMR"
                              :conceptId      "author"}]
                      :type "Segment"}]})

(def tiny-semantic
  {:nodes     #{{:type :ROOT :id "ROOT"}
                {:type :document-plan :id"document-plan"}
                {:type :segment :id "segment"}
                {:type :data :field :title :id "data-title"}
                {:type :modifier :dictionary :good :id "modifier-good"}}
   :relations #{["ROOT" :--> "document-plan"]
                ["document-plan" :--> "segment"]
                ["segment" :--> "data-title"]
                ["data-title" :--> "modifier-good"]}})

(def amr-semantic
  {:nodes     #{{:type :document-plan :id "document-plan"}
                {:type :segment :id "segment-1"}
                {:type :segment :id "segment-2"}
                {:type :quote :id "quote-1"}
                {:type :dictionary-item :name :written :id "dictionary-written"}
                {:type :modifier :dictionary :good :id "modifier-good"}
                {:type :data :field :authors :id "data-authors"}
                {:type :data :field :title :id "data-title"}}
   :relations #{["document-plan" :has "segment-1"]
                ["document-plan" :has "segment-2"]
                ["segment-1" :has "quote-1"]
                ["segment-2" :has "amr-author"]
                ["amr-author" :arg0 "data-authors"]
                ["amr-author" :arg1 "data-title"]
                ["amr-author" :event "event-written"]
                ["data-authors" :modified-by "modifier-good"]}})

(deftest dp->semantic
  (is (= tiny-semantic (parser/parse-document-plan tiny-dp)))
  #_(is (= amr-semantic (parser/parse-document-plan amr-dp))))