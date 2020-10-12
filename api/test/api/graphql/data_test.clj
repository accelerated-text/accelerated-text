(ns api.graphql.data-test
  (:require [api.test-utils :refer [q load-data-file]]
            [api.db-fixtures :as db]
            [clojure.string :as str]
            [clojure.test :refer [deftest is use-fixtures testing]]
            [data.entities.data-files :as data-files]))

(use-fixtures :each db/clean-db)

(deftest ^:integration rw
  (testing "that there are no data files"
    (let [query "{listDataFiles(offset:%s limit:%s recordOffset:%s recordLimit:%s){offset limit totalCount dataFiles{id fileName fieldNames}}}"
          {{{{:keys [totalCount]} :listDataFiles} :data errors :errors} :body}
          (q "/_graphql" :post {:query (format query 0 20 0 20)})]
      (is (nil? errors))
      (is (zero? totalCount))))

  (testing "writing data file to DDB"
    (let [query "mutation CreateDataFile($filename:String! $content:String! $id:ID){createDataFile(filename:$filename content:$content){id}}"
          {{{{id :id :as df} :createDataFile} :data errors :errors} :body}
          (q "/_graphql" :post {:query     query
                                :variables {:filename "books.csv"
                                            :content  (slurp "test/resources/data-files/books.csv")}})]
      (is (nil? errors))
      (is (= #{:id} (set (keys df))))
      (is (string? id))

      (testing "if data file from the previous tests is accessed by id"
        (let [query "{getDataFile(id:\"%s\" recordOffset:%s recordLimit:%s){id fileName fieldNames records{id fields{id fieldName value}} recordOffset recordLimit recordCount}}"
              {{{{:keys [recordLimit recordOffset recordCount fileName records fieldNames id]} :getDataFile} :data errors :errors} :body}
              (q "/_graphql" :post {:query (format query id 0 20)})]
          (is (nil? errors))
          (is (= 20 recordLimit))
          (is (zero? recordOffset))
          (is (pos-int? recordCount))
          (is (= "books.csv" fileName))
          (is (seq records))
          (is (seq fieldNames))
          (is (string? id))))))

  (testing "that there is one data file"
    (let [query "{listDataFiles(offset:%s limit:%s recordOffset:%s recordLimit:%s){offset limit totalCount dataFiles{id fileName fieldNames}}}"
          {{{{:keys [totalCount]} :listDataFiles} :data errors :errors} :body}
          (q "/_graphql" :post {:query (format query 0 20 0 20)})]
      (is (nil? errors))
      (is (= 1 totalCount)))))

(deftest ^:integration reading-data-file-contents
  (testing "Read books.csv headers"
    (let [data-file-id (load-data-file "books.csv")
          result (data-files/read-data-file data-file-id)
          headers (-> result (get :content) (str/split-lines) (first) (str/split #",") (set))]
      (is (= #{"authors" "categories" "pageCount" "publishedDate" "publisher" "subtitle" "title"} headers)))))
