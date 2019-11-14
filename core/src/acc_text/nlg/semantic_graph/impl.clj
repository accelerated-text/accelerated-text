(ns acc-text.nlg.semantic-graph.impl
  (:require [clojure.string :as str])
  (:import (java.util UUID)))

(def default-context
  #:acc-text.nlg.semantic-graph{:amr              {}
                                :dictionary       {:default {}}
                                :document-plan-id (str (UUID/randomUUID))
                                :reader-profiles  [:default]})

(defmulti add-context (fn [concept _] (get concept :acc-text.nlg.semantic-graph/type)))

(defmethod add-context :default [concept _] concept)

(defmethod add-context :dictionary-item [{value :acc-text.nlg.semantic-graph/value :as concept}
                                         {:acc-text.nlg.semantic-graph/keys [dictionary reader-profile]}]
  (-> concept
      (assoc :acc-text.nlg.semantic-graph/members (get dictionary value))
      (assoc-in [:acc-text.nlg.semantic-graph/attributes
                 :acc-text.nlg.semantic-graph/reader-profile] reader-profile)))

(defmethod add-context :amr [{value :acc-text.nlg.semantic-graph/value :as concept} {amr :acc-text.nlg.semantic-graph/amr}]
  (assoc-in concept [:acc-text.nlg.semantic-graph/attributes
                     :acc-text.nlg.semantic-graph/syntax] (->> (keyword value) (get amr) (:frames) (map :syntax))))

(defn ->instance-id [document-plan-id reader-profile]
  (keyword (str/join "-" (remove nil? [document-plan-id (when (some? reader-profile) (name reader-profile))]))))

(defn context->instance-context [context reader-profile]
  (-> context
      (update :acc-text.nlg.semantic-graph/dictionary #(get % reader-profile))
      (assoc :acc-text.nlg.semantic-graph/reader-profile reader-profile)
      (dissoc :acc-text.nlg.semantic-graph/reader-profiles)))

(defn build-instances
  ([semantic-graph]
   (build-instances semantic-graph default-context))
  ([semantic-graph {:acc-text.nlg.semantic-graph/keys [reader-profiles document-plan-id] :as context}]
   (for [reader-profile reader-profiles]
     (let [instance-context (context->instance-context context reader-profile)]
       #:acc-text.nlg.semantic-graph{:id      (->instance-id document-plan-id reader-profile)
                                     :context instance-context
                                     :graph   (update semantic-graph
                                                      :acc-text.nlg.semantic-graph/concepts
                                                      (partial map #(add-context % instance-context)))}))))
