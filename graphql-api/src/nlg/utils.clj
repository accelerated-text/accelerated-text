(ns nlg.utils
  (:require [clojure.tools.logging :as log]
            [clj-time.core :as time]
            [clj-time.coerce :as tc])
  (:import (java.util UUID)))

(defn gen-uuid [] (.toString (UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))

(defn stack-trace [e]
  (reduce #(str %1 "\n" %2) (.getStackTrace ^Exception e)))

(defn do-return [func & args]
  (try
    (let [resp (apply func args)]
      (if resp
        (if (contains? resp :error)
          {:status 500
           :body   {:error true :message "ERROR_01"}}
          {:status 200
           :body   resp})
        {:status 404}))
    (catch Exception e
      (log/errorf "Exception caught in utils/do-return '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn do-insert [func & args]
  (try
    (let [id (gen-uuid)
          insert-fn (partial func id)
          resp (apply insert-fn args)]
      {:status 200
       :body   resp})
    (catch Exception e
      (log/errorf "Exception caught in utils/do-insert '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn do-delete [search-fn delete-fn & args]
  (try
    (let [original (apply search-fn args)]
      (apply delete-fn args)
      {:status 200
       :body   original})
    (catch Exception e
      (log/errorf "Exception caught in utils/do-delete '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn do-update [func & args]
  (try
    (let [resp (apply func args)]
      {:status 200
       :body   resp})
    (catch Exception e
      (log/errorf "Exception caught in utils/do-update '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn add-status [resp-vec]
  {:status (if (every? #(= 200 (get % :status)) resp-vec) 200 500)
   :body   resp-vec})
