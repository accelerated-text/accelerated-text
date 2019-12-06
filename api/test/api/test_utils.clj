(ns api.test-utils
  (:require [api.server :as server]
            [api.utils :as utils]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [jsonista.core :as json]
            [clojure.tools.logging :as log])
  (:import (java.io PushbackReader)
           (org.httpkit BytesInputStream)))

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

(defn load-test-document-plan [filename]
  (with-open [r (io/reader (format "test/resources/document_plans/%s.edn" filename))]
    (edn/read (PushbackReader. r))))

(defn rebuild-sentence [tokens]
  (->> tokens
       (map (fn [{type :type text :text}]
          (case (keyword type)
            :WORD (str " " text)
            :PUNCTUATION (if (re-find #"^[.,?!:]" text)
                           text
                           (str " " text)))))
       (apply str)
       (string/trim)))
