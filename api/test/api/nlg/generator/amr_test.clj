(ns api.nlg.generator.amr-test
  (:require [acc-text.nlg.grammar :as grammar]
            [acc-text.nlg.verbnet.ccg :as verbnet.ccg]
            [clojure.test :refer [deftest is]]
            [clojure.tools.logging :as log]
            [data.entities.amr :as amr-entity]))

(deftest test-author-amr
  (let [vn (amr-entity/get-verbclass :author)
        grammars (verbnet.ccg/vn->grammar vn)
        result (-> (map (fn [g] (grammar/generate g "{{AGENT}}" "{{CO-AGENT}}" "{{THEME}}")) grammars)
                   (flatten)
                   (set))]
    (log/debugf "Got verblcass: %s" vn)
    (log/debugf "Result: %s" (pr-str result))
    (is (contains? result "{{AGENT}} is the author of {{CO-AGENT}}"))))

