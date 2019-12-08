(ns api.books-scenario-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [api.db-fixtures :as db]
            [api.server :refer [app]]
            [jsonista.core :as json]
            [api.utils :refer [read-mapper]])
  (:import (java.io ByteArrayInputStream)))

(use-fixtures :each db/clean-db)

(defn obj->json-string [obj]
  (if (string? obj)
    (-> obj
        (.getBytes "UTF-8")
        (ByteArrayInputStream.))
    (-> (json/write-value-as-string obj))))

(defn keys-in [m]
  (if (map? m)
    (vec
      (mapcat (fn [[k v]]
                (let [sub (keys-in v)
                      nested (map #(into [k] %) (filter (comp not empty?) sub))]
                  (if (seq nested)
                    nested
                    [[k]])))
              m))
    []))

(defn call [req]
  (-> (app (assoc req :body (obj->json-string (:body req))))
      (update :body #(json/read-value % read-mapper))))

(deftest ^:integration data-file-upload
  (let [req0 {:uri "/_graphql", :params {}, :body {:operationName "dictionary", :variables {}, :query "query dictionary {\n  dictionary {\n    offset\n    totalCount\n    items {\n      ...dictionaryItemFields\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment dictionaryItemFields on DictionaryItem {\n  id\n  name\n  partOfSpeech\n  concept {\n    ...conceptFields\n    __typename\n  }\n  phrases {\n    ...phraseFields\n    __typename\n  }\n  __typename\n}\n\nfragment conceptFields on Concept {\n  id\n  label\n  helpText\n  roles {\n    ...thematicRoleFields\n    __typename\n  }\n  __typename\n}\n\nfragment phraseFields on Phrase {\n  id\n  text\n  defaultUsage\n  readerFlagUsage {\n    ...readerFlagUsageFields\n    __typename\n  }\n  __typename\n}\n\nfragment thematicRoleFields on ThematicRole {\n  id\n  fieldLabel\n  fieldType\n  __typename\n}\n\nfragment readerFlagUsageFields on ReaderFlagUsage {\n  id\n  flag {\n    ...readerFlagFields\n    __typename\n  }\n  usage\n  __typename\n}\n\nfragment readerFlagFields on ReaderFlag {\n  id\n  name\n  __typename\n}\n"}, :request-method :post}
        req1 {:uri "/_graphql", :params {}, :body {:operationName "concepts", :variables {}, :query "query concepts {\n  concepts {\n    id\n    concepts {\n      ...conceptFields\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment conceptFields on Concept {\n  id\n  label\n  helpText\n  roles {\n    ...thematicRoleFields\n    __typename\n  }\n  __typename\n}\n\nfragment thematicRoleFields on ThematicRole {\n  id\n  fieldLabel\n  fieldType\n  __typename\n}\n"}, :request-method :post}
        req2 {:uri "/_graphql", :params {}, :body {:operationName "listDataFiles", :variables {}, :query "query listDataFiles {\n  listDataFiles {\n    offset\n    totalCount\n    limit\n    dataFiles {\n      id\n      fileName\n      fieldNames\n      __typename\n    }\n    __typename\n  }\n}\n"}, :request-method :post}
        req3 {:uri "/_graphql", :params {}, :body {:operationName "readerFlags", :variables {}, :query "query readerFlags {\n  readerFlags {\n    id\n    flags {\n      ...readerFlagFields\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment readerFlagFields on ReaderFlag {\n  id\n  name\n  __typename\n}\n"}, :request-method :post}
        req4 {:uri "/_graphql", :params {}, :body {:operationName "documentPlans", :variables {}, :query "query documentPlans {\n  documentPlans {\n    offset\n    totalCount\n    limit\n    items {\n      ...documentPlanFields\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment documentPlanFields on DocumentPlan {\n  id\n  uid\n  name\n  blocklyXml\n  documentPlan\n  dataSampleId\n  dataSampleRow\n  createdAt\n  updatedAt\n  updateCount\n  __typename\n}\n"}, :request-method :post}
        req5 {:uri "/_graphql", :params {}, :body {:operationName "createDocumentPlan", :variables {:blocklyXml "<xml xmlns=\"http://www.w3.org/1999/xhtml\"><block type=\"Document-plan\" deletable=\"false\"><statement name=\"segments\"><block type=\"Segment\"><mutation value_count=\"2\" value_sequence=\"value_\"></mutation></block></statement></block></xml>", :__typename "DocumentPlan", :uid "06b6a85c-7696-4d1f-9775-84692cace0cd", :name "Untitled plan", :contextId nil, :createdAt 1573023619002, :dataSampleId nil, :documentPlan "{\"type\":\"Document-plan\",\"srcId\":\"new-document-plan\",\"segments\":[{\"type\":\"Segment\",\"srcId\":\"new-segment\",\"children\":[]}]}", :dataSampleRow 0, :updateCount 0}, :query "mutation createDocumentPlan($uid: ID!, $name: String!, $blocklyXml: String!, $documentPlan: String!, $dataSampleId: ID, $dataSampleRow: Int) {\n  createDocumentPlan(uid: $uid, name: $name, blocklyXml: $blocklyXml, documentPlan: $documentPlan, dataSampleId: $dataSampleId, dataSampleRow: $dataSampleRow) {\n    ...documentPlanFields\n    __typename\n  }\n}\n\nfragment documentPlanFields on DocumentPlan {\n  id\n  uid\n  name\n  blocklyXml\n  documentPlan\n  dataSampleId\n  dataSampleRow\n  createdAt\n  updatedAt\n  updateCount\n  __typename\n}\n"}, :request-method :post}
        req6 {:uri "/_graphql", :params {}, :body {:operationName "documentPlans", :variables {}, :query "query documentPlans {\n  documentPlans {\n    offset\n    totalCount\n    limit\n    items {\n      ...documentPlanFields\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment documentPlanFields on DocumentPlan {\n  id\n  uid\n  name\n  blocklyXml\n  documentPlan\n  dataSampleId\n  dataSampleRow\n  createdAt\n  updatedAt\n  updateCount\n  __typename\n}\n"}, :request-method :post}
        req7 {:uri "/accelerated-text-data-files/",
              :params {"key" "books.csv", "acl" "public-read", "file" {:filename "books.csv", :content-type "text/csv", :content "id,isbn-13,title,subtitle,authors,publisher,publishedDate,pageCount,categories,averageRating,ratingsCount,maturityRating,thumbnail,language\n9780615204253,9780615204253,\"Building Search Applications\",\"Lucene, LingPipe, and Gate\",\"Manu Konchady\",Lulu.com,2008,430,Computers,,,NOT_MATURE,http://books.google.com/books/content?id=yNnpAgAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api,en\n9781597497251,9781597497251,\"Moving to the Cloud\",\"Developing Apps in the New World of Cloud Computing\",\"Dinkar Sitaram, Geetha Manjunath\",Elsevier,2011,448,Computers,,,NOT_MATURE,http://books.google.com/books/content?id=Nq4J0_tKOlsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api,en\n9780956599315,9780956599315,\"Text Processing with GATE\",\"Version 6\",\"Hamish Cunningham, Kalina Bontcheva, Diana Maynard\",Gate,2011,573,Computers,,,NOT_MATURE,http://books.google.com/books/content?id=vM42KQEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api,en\n9781250065636,9781250065636,\"The Age of Cryptocurrency\",\"How Bitcoin and Digital Money Are Challenging the Global Economic Order\",\"Paul Vigna, Michael J. Casey\",Macmillan,2015-01-27,368,\"Business & Economics\",,,NOT_MATURE,http://books.google.com/books/content?id=Jjy3BQAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api,en\n9781119300311,9781119300311,\"The Business Blockchain\",\"Promise, Practice, and Application of the Next Internet Technology\",\"William Mougayar\",\"John Wiley & Sons\",2016-05-09,208,\"Business & Economics\",5.0,1,NOT_MATURE,http://books.google.com/books/content?id=X8oXDAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api,en\n"}},
              :headers {"origin" "http://localhost:8080",
                        "host" "0.0.0.0:3001",
                        "user-agent" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0",
                        "content-type" "multipart/form-data; boundary=---------------------------704885301643800608931319847",
                        "content-length" "2075",
                        "referer" "http://localhost:8080/",
                        "connection" "keep-alive",
                        "accept" "*/*",
                        "accept-language" "en-US,en;q=0.5",
                        "accept-encoding" "gzip, deflate"}
              :content-length 2075
              :content-type "multipart/form-data; boundary=---------------------------704885301643800608931319847",
              :character-encoding "utf8"
              :body "-----------------------------20858548701865296731267479087\r\nContent-Disposition: form-data; name=\"key\"\r\n\r\nbooks.csv\r\n-----------------------------20858548701865296731267479087\r\nContent-Disposition: form-data; name=\"acl\"\r\n\r\npublic-read\r\n-----------------------------20858548701865296731267479087\r\nContent-Disposition: form-data; name=\"file\"; filename=\"books.csv\"\r\nContent-Type: text/csv\r\n\r\nid,isbn-13,title,subtitle,authors,publisher,publishedDate,pageCount,categories,averageRating,ratingsCount,maturityRating,thumbnail,language\n9780615204253,9780615204253,\"Building Search Applications\",\"Lucene, LingPipe, and Gate\",\"Manu Konchady\",Lulu.com,2008,430,Computers,,,NOT_MATURE,http://books.google.com/books/content?id=yNnpAgAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api,en\n9781597497251,9781597497251,\"Moving to the Cloud\",\"Developing Apps in the New World of Cloud Computing\",\"Dinkar Sitaram, Geetha Manjunath\",Elsevier,2011,448,Computers,,,NOT_MATURE,http://books.google.com/books/content?id=Nq4J0_tKOlsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api,en\n9780956599315,9780956599315,\"Text Processing with GATE\",\"Version 6\",\"Hamish Cunningham, Kalina Bontcheva, Diana Maynard\",Gate,2011,573,Computers,,,NOT_MATURE,http://books.google.com/books/content?id=vM42KQEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api,en\n9781250065636,9781250065636,\"The Age of Cryptocurrency\",\"How Bitcoin and Digital Money Are Challenging the Global Economic Order\",\"Paul Vigna, Michael J. Casey\",Macmillan,2015-01-27,368,\"Business & Economics\",,,NOT_MATURE,http://books.google.com/books/content?id=Jjy3BQAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api,en\n9781119300311,9781119300311,\"The Business Blockchain\",\"Promise, Practice, and Application of the Next Internet Technology\",\"William Mougayar\",\"John Wiley & Sons\",2016-05-09,208,\"Business & Economics\",5.0,1,NOT_MATURE,http://books.google.com/books/content?id=X8oXDAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api,en\n\r\n-----------------------------20858548701865296731267479087--\r\n",
              :request-method :post}
        req8 {:uri "/_graphql", :params {}, :body {:operationName "listDataFiles", :variables {}, :query "query listDataFiles {\n  listDataFiles {\n    offset\n    totalCount\n    limit\n    dataFiles {\n      id\n      fileName\n      fieldNames\n      __typename\n    }\n    __typename\n  }\n}\n"}, :request-method :post}
        req9 {:uri "/_graphql", :params {}, :body {:operationName "getDataFile", :variables {:id "11898ba2-bc52-402f-aa78-5b09b1db8ef8"}, :query "query getDataFile($id: ID!) {\n  getDataFile(id: $id) {\n    id\n    fileName\n    fieldNames\n    records {\n      id\n      fields {\n        id\n        fieldName\n        value\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }\n}\n"}, :request-method :post}
        req10 {:uri "/_graphql", :params {}, :body {:operationName "updateDocumentPlan", :variables {:blocklyXml "<xml xmlns=\"http://www.w3.org/1999/xhtml\"><block type=\"Document-plan\" deletable=\"false\"><statement name=\"segments\"><block type=\"Segment\"><mutation value_count=\"2\" value_sequence=\"value_\"></mutation></block></statement></block></xml>", :updatedAt 1573023619, :__typename "DocumentPlan", :uid "06b6a85c-7696-4d1f-9775-84692cace0cd", :name "Untitled plan", :createdAt 1573023619, :dataSampleId "11898ba2-bc52-402f-aa78-5b09b1db8ef8", :id "e3e784e2-310c-49e9-9ddf-e0bd3bebbce0", :documentPlan "{\"type\":\"Document-plan\",\"segments\":[{\"children\":[],\"type\":\"Segment\",\"srcId\":\"new-segment\"}],\"srcId\":\"new-document-plan\"}", :dataSampleRow 0, :updateCount 0}, :query "mutation updateDocumentPlan($id: ID!, $uid: ID, $name: String, $blocklyXml: String, $documentPlan: String, $dataSampleId: ID, $dataSampleRow: Int) {\n  updateDocumentPlan(id: $id, uid: $uid, name: $name, blocklyXml: $blocklyXml, documentPlan: $documentPlan, dataSampleId: $dataSampleId, dataSampleRow: $dataSampleRow) {\n    ...documentPlanFields\n    __typename\n  }\n}\n\nfragment documentPlanFields on DocumentPlan {\n  id\n  uid\n  name\n  blocklyXml\n  documentPlan\n  dataSampleId\n  dataSampleRow\n  createdAt\n  updatedAt\n  updateCount\n  __typename\n}\n"}, :request-method :post}
        req11 {:uri "/nlg/",
               :params {},
               :body {:documentPlanId "e3e784e2-310c-49e9-9ddf-e0bd3bebbce0",
                      :readerFlagValues {},
                      :dataId "11898ba2-bc52-402f-aa78-5b09b1db8ef8"},
               :content-type "application/json",
               :request-method :post}]
    (is (= [[:data :dictionary :__typename] [:data :dictionary :offset] [:data :dictionary :totalCount] [:data :dictionary :items]]
           (-> (call req0) :body keys-in)))
    (is (= [[:data :concepts :__typename] [:data :concepts :concepts] [:data :concepts :id]]
           (-> (call req1) :body keys-in)))
    (is (= [[:data
             :listDataFiles
             :dataFiles]
            [:data
             :listDataFiles
             :__typename]
            [:data
             :listDataFiles
             :limit]
            [:data
             :listDataFiles
             :offset]
            [:data
             :listDataFiles
             :totalCount]]
           (-> (call req2) :body keys-in)))
    (is (= [[:data
             :readerFlags
             :__typename]
            [:data
             :readerFlags
             :id]
            [:data
             :readerFlags
             :flags]]
           (-> (call req3) :body keys-in)))
    (is (= [[:data
             :documentPlans
             :__typename]
            [:data
             :documentPlans
             :limit]
            [:data
             :documentPlans
             :offset]
            [:data
             :documentPlans
             :totalCount]
            [:data
             :documentPlans
             :items]]
           (-> (call req4) :body keys-in)))
    (let [{body :body :as resp} (call req5)
          document-plan-id (-> body :data :createDocumentPlan :id)]
      (is (= [[:data
               :createDocumentPlan
               :blocklyXml]
              [:data
               :createDocumentPlan
               :updatedAt]
              [:data
               :createDocumentPlan
               :__typename]
              [:data
               :createDocumentPlan
               :uid]
              [:data
               :createDocumentPlan
               :name]
              [:data
               :createDocumentPlan
               :createdAt]
              [:data
               :createDocumentPlan
               :dataSampleId]
              [:data
               :createDocumentPlan
               :id]
              [:data
               :createDocumentPlan
               :documentPlan]
              [:data
               :createDocumentPlan
               :dataSampleRow]
              [:data
               :createDocumentPlan
               :updateCount]]
             (-> resp :body keys-in)))
      (is (= [[:data
               :documentPlans
               :__typename]
              [:data
               :documentPlans
               :limit]
              [:data
               :documentPlans
               :offset]
              [:data
               :documentPlans
               :totalCount]
              [:data
               :documentPlans
               :items]]
             (-> (call req6) :body keys-in)))
      (is (= [[:id]
              [:message]]
             (-> (call req7) :body keys-in)))
      (let [resp (call req8)
            data-file-id (-> resp :body :data :listDataFiles :dataFiles first :id)]
        (is (= [[:data
                 :listDataFiles
                 :dataFiles]
                [:data
                 :listDataFiles
                 :__typename]
                [:data
                 :listDataFiles
                 :limit]
                [:data
                 :listDataFiles
                 :offset]
                [:data
                 :listDataFiles
                 :totalCount]]
               (-> resp :body keys-in)))
        (let [resp (call (assoc-in req9 [:body :variables :id] data-file-id))]
          (clojure.pprint/pprint resp)
          (is (= [[:data
                   :getDataFile
                   :__typename]
                  [:data
                   :getDataFile
                   :fileName]
                  [:data
                   :getDataFile
                   :records]
                  [:data
                   :getDataFile
                   :fieldNames]
                  [:data
                   :getDataFile
                   :id]]
                 (-> resp :body keys-in))))
        (is (= [[:data
                 :updateDocumentPlan
                 :blocklyXml]
                [:data
                 :updateDocumentPlan
                 :updatedAt]
                [:data
                 :updateDocumentPlan
                 :__typename]
                [:data
                 :updateDocumentPlan
                 :uid]
                [:data
                 :updateDocumentPlan
                 :name]
                [:data
                 :updateDocumentPlan
                 :createdAt]
                [:data
                 :updateDocumentPlan
                 :dataSampleId]
                [:data
                 :updateDocumentPlan
                 :id]
                [:data
                 :updateDocumentPlan
                 :documentPlan]
                [:data
                 :updateDocumentPlan
                 :dataSampleRow]
                [:data
                 :updateDocumentPlan
                 :updateCount]]
               (-> (call (assoc-in req10 [:body :variables :id] document-plan-id)) :body keys-in)))
        (is (= [[:resultId]]
               (-> (call (-> req11
                             (assoc-in [:body :documentPlanId] document-plan-id)
                             (assoc-in [:body :dataId] data-file-id)))
                   :body (doto prn) keys-in)))))))

