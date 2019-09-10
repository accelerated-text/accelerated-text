(ns nlg.generator.amr-test
  (:require [clojure.test :refer :all]
            [data-access.entities.amr :as amr-entity]
            [clojure.tools.logging :as log]
            [ccg-kit.verbnet.ccg :refer :all]
            [ccg-kit.grammar :as grammar]))

(deftest test-author-amr
  (let [vn (amr-entity/get-verbclass :author)
        grammars (vn->grammar vn)
        result (-> (map (fn [g] (grammar/generate g "{{AGENT}}" "{{CO-AGENT}}" "{{THEME}}")) grammars)
                   (flatten)
                   (set))]
    (log/debugf "Got verblcass: %s" vn)
    (log/debugf "Result: %s" (pr-str result))
    (is (contains? result "{{AGENT}} is the author of {{CO-AGENT}}"))))

