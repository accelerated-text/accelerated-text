(ns importer.core
  (:gen-class)
  (:require [db.config :as config]
            [taoensso.faraday :as far]
            [nlg.lexicon :as lexicon]))

(defn -main [& args]
  (with-redefs [config/client-opts (fn [] {:endpoint "http://localhost:8000"
                                     :profile  "tm"})]
    (far/create-table config/client-opts :lexicon [:key :s])
    (lexicon/create {:synonyms ["good" "amazing" "superb"]})))
