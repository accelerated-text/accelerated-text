(ns data.db.dynamoops-test
  (:require [clojure.test :refer [deftest is]]
            [data.protocol :as ops]
            [data.db :as db]
            [data.utils :as utils]))

(def mock-db
  (reify ops/DBAccess
    (read-item [this key])
    (write-item [this key data update-count?]
      (let [body (-> data
                     (assoc :key key)
                     (assoc :createdAt (utils/ts-now))
                     (assoc :updatedAt (utils/ts-now)))]
        body))
    (update-item [this key data])
    (delete-item [this key])
    (list-items [this limit])))

(deftest test-write-wo-key
  (let [db mock-db
        result (db/write! db {:something "somedata"})]
    (is (seq result))
    (is (:something result))))

(deftest test-write-w-key
  (let [db mock-db
        result (db/write! db "testKey" {:something "somedata"})]
    (is (seq result))
    (is (= "testKey" (:key result)))
    (is (:something result))))
