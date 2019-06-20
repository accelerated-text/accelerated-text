(ns translate.dictionary
  (:require [clojure.tools.logging :as log]))

(defn phrase->schema
  [phrase]
  (log/tracef "Phrase: %s" phrase)
  {:id (:id phrase)
   :text (:phrase phrase)
   :defaultUsage (:defaultUsage phrase)
   :readerFlagUsage (:readerFlagUsage phrase)})
