(ns api.graphql.data-test
  (:require [api.test-utils :refer [q]]
            [clojure.test :refer [deftest is]]))

(deftest ^:integration get-data-test
  (let [query "{getDataFile(id:\"%s\" recordOffset:%s recordLimit:%s){id fileName fieldNames records{id fields{id fieldName value}} recordOffset recordLimit recordCount}}"
        {{{{:keys [recordLimit recordOffset recordCount fileName records fieldNames id]} :getDataFile} :data errors :errors} :body}
        (q "/_graphql" :post {:query (format query "example-user/books.csv" 0 20)})]
    (is (nil? errors))
    (is (= 20 recordLimit))
    (is (zero? recordOffset))
    (is (pos-int? recordCount))
    (is (= "books.csv" fileName))
    (is (seq records))
    (is (seq fieldNames))
    (is (= "example-user/books.csv" id))))

(deftest ^:integration list-data-test
  (let [query "{listDataFiles(offset:%s limit:%s recordOffset:%s recordLimit:%s){offset limit totalCount dataFiles{id fileName fieldNames}}}"
        {{{{:keys [offset limit totalCount dataFiles]} :listDataFiles} :data errors :errors} :body}
        (q "/_graphql" :post {:query (format query 0 20 0 20)})]
    (is (nil? errors))
    (is (zero? offset))
    (is (= 20 limit))
    (is (pos-int? totalCount))
    (is (>= 20 totalCount))
    (is (seq dataFiles))))
