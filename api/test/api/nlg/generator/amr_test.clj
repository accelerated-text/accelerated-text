(ns api.nlg.generator.amr-test
  (:require [acc-text.nlg.grammar :as grammar]
            [acc-text.nlg.verbnet.ccg :as verbnet.ccg]
            [clojure.test :refer [deftest is]]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [data.entities.amr :as amr]))

(deftest test-author-amr
  (let [vn (amr/read-amr (io/file "test/resources/amr/author.yaml"))
        grammars (verbnet.ccg/vn->grammar vn)
        result (-> (map (fn [g] (grammar/generate g "{{AGENT}}" "{{CO-AGENT}}")) grammars)
                   (flatten)
                   (set))]
    (log/debugf "Got verblcass: %s" vn)
    (log/debugf "Result: %s" (pr-str result))
    (is (contains? result "{{AGENT}} is the author of {{CO-AGENT}}"))))
