(ns api.test-utils
  (:require [api.server :as server]
            [api.utils :as utils]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files])
  (:import (org.httpkit BytesInputStream)))

(def headers {"origin"                         "http://localhost:8080"
              "host"                           "0.0.0.0:3001"
              "user-agent"                     "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0"
              "access-control-request-headers" "content-type"
              "referer"                        "http://localhost:8080/"
              "connection"                     "keep-alive"
              "accept"                         "*/*"
              "accept-language"                "en-US,en;q=0.5"
              "content-type"                   "application/json"
              "access-control-request-method"  "POST"
              "accept-encoding"                "gzip, deflate"
              "dnt"                            "1"})

(defn encode [body]
  (let [content (json/write-value-as-string body)]
    (BytesInputStream. (.getBytes content) (count content))))

(defn handle-http-error [{:keys [status body] :as resp}]
  (when-not (= 200 status)
    (log/errorf "Error: %s" body))
  resp)

(defn q
  ([uri method body]
   (q uri method body {}))
  ([uri method body query]
   (log/debugf "Doing request %s to URL: %s Body: %s" method uri body)
   (log/spyf :debug "Response: %s"
             (-> {:uri uri :request-method method :body body :query-params query}
                 (assoc :headers headers)
                 (update :body encode)
                 (server/app)
                 (update :body #(json/read-value % utils/read-mapper))
                 (handle-http-error)))))

(defn load-data-file [filename]
  (data-files/store!
    {:filename filename
     :content  (slurp (format "test/resources/data-files/%s" filename))}))

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil {:format "raw"}) [:body :ready]))
    (Thread/sleep 100)))

(defn get-variants [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil {:format "raw"})
          variants (get-in response [:body :variants])]
      (into {} (map (fn [item] (let [[k v] item] {(keyword k) (set v)})) variants)))))

(defn get-original-results [result-id]
  (->>
    result-id
    (get-variants)
    :sample
    (map :original)
    (set)))

(defn generate-text
  ([document-plan-id]
   (generate-text document-plan-id {"English" true}))
  ([document-plan-id reader-flags]
   (generate-text document-plan-id reader-flags nil))
  ([document-plan-id reader-flags data-id]
   (q "/nlg/" :post {:documentPlanId   document-plan-id
                     :readerFlagValues reader-flags
                     :dataId           data-id})))
