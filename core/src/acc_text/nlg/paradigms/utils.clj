(ns acc-text.nlg.paradigms.utils
  (:require [clojure.set :as set]
            [clojure.string :as str])
  (:import (java.util UUID)))

(defn gen-id []
  (str (UUID/randomUUID)))

(defn get-label [module name args category]
  (format "%s.%s/%s" module name (str/join "->" (conj (vec args) category))))

(defn find-root [{::sg/keys [concepts relations]}]
  (first (set/difference (set (map :id concepts)) (map :to relations))))
