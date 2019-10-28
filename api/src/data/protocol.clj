(ns data.protocol)

(defprotocol DBAccess
  (read-item [this key])
  (write-item [this key data update-count?])
  (update-item [this key data])
  (delete-item [this key])
  (list-items [this limit])
  (scan-items [this opts])
  (batch-read-items [this opts]))
