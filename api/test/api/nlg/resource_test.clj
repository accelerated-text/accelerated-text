(ns api.nlg.resource-test
  (:require [api.nlg.resource :as resource]
            [clojure.test :refer [deftest is]]))

(deftest decode-vals-test
  (is (= nil (resource/decode-vals nil)))
  (is (= {} (resource/decode-vals {})))
  (is (= {:id "a bit.1"} (resource/decode-vals {:id "a%20bit.1"}))))
