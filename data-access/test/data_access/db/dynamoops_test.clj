(ns data-access.db.dynamoops-test
  (:require [clojure.test :refer :all]
            [data-access.db.dynamo-ops :as ops]
            [data-access.utils :as utils]))

(def mock-db
  (reify ops/DBAccess
    (read-item [this key] ())
    (write-item [this key data]
      (let [body (-> data
                     (assoc :key key)
                     (assoc :createdAt (utils/ts-now))
                     (assoc :updatedAt (utils/ts-now)))]
        body))
    (update-item [this key data] ())
    (delete-item [this key] ())
    (list-items [this limit] ())))

(deftest test-write-wo-key
  (let [db mock-db
        result (ops/write! db {:something "somedata"})]
    (is (not (empty? result)))
    (is (:something result))))

(deftest test-write-w-key
  (let [db mock-db
        result (ops/write! db "testKey" {:something "somedata"})]
    (is (not (empty? result)))
    (is (= "testKey" (:key result)))
    (is (:something result))))


