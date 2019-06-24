(ns data-access.db.datomic-ops
  (:require [datomic.client.api :as datomic]
            [data-access.db.config :as config]
            [data-access.utils :as utils]
            [clojure.tools.logging :as log]))


(defn connect [connection-str]
  (datomic/connect connection-str))

(defprotocol DBAccess
  (read-item [this key]))

(defn read! [this key] (read-item this key))


(defn db-access
  [table-name connection]
  (reify
    DBAccess
    (read-item [this key]
      )))


;; (defn db-access
;;   [resource-type]
;;   (let [{table-name :table-name
;;          table-key :table-key} (resolve-table resource-type)]
;;     (reify
;;       DBAccess
;;       (read-item [this key]
;;         (far/get-item (config/client-opts) table-name {table-key key}))
;;       (write-item [this key data]
;;         (log/debugf "Writing\n key: '%s' \n content: '%s'" key data)
;;         (let [body (-> data
;;                        (assoc table-key key)
;;                        (assoc :createdAt (utils/ts-now))
;;                        (assoc :updatedAt (utils/ts-now)))
;;               normalized (doall (normalize body))]
;;           (do
;;             (far/put-item (config/client-opts) table-name normalized)
;;             body)))
;;       (update-item [this key data]
;;         (log/debugf "Updating\n key: '%s' \n content: '%s'" key data)
;;         (let [original (far/get-item (config/client-opts) table-name {table-key key})
;;               body (-> (merge original data)
;;                        (assoc :updatedAt (utils/ts-now))
;;                        (assoc :key key))]
;;           (do
;;             (far/put-item (config/client-opts) table-name body)
;;             body)))
;;       (delete-item [this key]
;;         (log/debugf "Deleting\n key: '%s'" key)
;;         (far/delete-item (config/client-opts) table-name {table-key key}))
;;       (list-items [this limit]
;;         (far/scan (config/client-opts) table-name {:limit limit}))
;;       (scan-items [this opts]
;;         (far/scan (config/client-opts) table-name opts))
;;       (batch-read-items [this opts]
;;         (far/batch-get-item (config/client-opts) {table-name opts})))))
