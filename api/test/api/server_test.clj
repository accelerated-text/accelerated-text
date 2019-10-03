(ns api.server-test
  (:require [api.server :as server]
            [clojure.test :refer [deftest is testing]]
            [cheshire.core :as json])
  (:import (org.httpkit BytesInputStream)))

(defn encode [body]
  (let [content (json/encode body)]
    (BytesInputStream. (.getBytes content) (count content))))

(deftest ^:integration server-test
  (let [headers {"origin"                         "http://localhost:8080"
                 "host"                           "0.0.0.0:3001"
                 "user-agent"                     "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0"
                 "access-control-request-headers" "content-type"
                 "referer"                        "http://localhost:8080/"
                 "connection"                     "keep-alive"
                 "accept"                         "*/*"
                 "accept-language"                "en-US,en;q=0.5"
                 "access-control-request-method"  "POST"
                 "accept-encoding"                "gzip, deflate"
                 "dnt"                            "1"}]
    (testing "GraphQL endpoint test"
      (let [request {:uri "/_graphql" :headers headers :request-method :options}
            response (server/app request)]
        (is (= 200 (:status response)))
        (is (= server/headers (:headers response))))
      (let [request {:body           {:operationName "documentPlans"
                                      :variables     {}
                                      :query         "bad query"}
                     :uri            "/_graphql"
                     :headers        headers
                     :request-method :post}
            response (server/app (update request :body encode))
            {[{message :message}] :errors} (json/decode (:body response) true)]
        (is (= 200 (:status response)))
        (is (= "Failed to parse GraphQL query." message))))
    (testing "NLG endpoint test"
      (let [request {:body           {:dataId           "example-user/books.csv"
                                      :documentPlanId   "test"
                                      :readerFlagValues {}}
                     :uri            "/nlg/"
                     :request-method :post}
            response (server/app (update request :body encode))
            {result-id :resultId} (json/decode (:body response) true)]
        (is (= 200 (:status response)))
        (is (some? result-id))))))
