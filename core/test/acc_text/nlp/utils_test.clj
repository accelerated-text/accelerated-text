(ns acc-text.nlp.utils-test
  (:require [acc-text.nlp.utils :as utils]
            [clojure.test :refer [deftest is]]))

(deftest tokenize-two-words
  (is (= ["hello" "world"] (utils/tokenize "hello world"))))

(deftest tokenize-russian-word
  (is (= ["местo"] (utils/tokenize "местo"))))
