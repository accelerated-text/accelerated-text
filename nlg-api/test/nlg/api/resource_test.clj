(ns nlg.api.resource-test
  (:require [clojure.test :refer :all]
            [nlg.api.resource :as resource]))

(deftest decode-vals-test
  (is (= nil (resource/decode-vals nil)))
  (is (= {} (resource/decode-vals {})))
  (is (= {:id "a bit.1"} (resource/decode-vals {:id "a%20bit.1"}))))
