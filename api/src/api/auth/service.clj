(ns api.auth.service
  (:require [api.config :refer [conf]]
            [org.httpkit.client :as client]
            [jsonista.core :as json]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn auth-enabled?
  []
  (not (nil? (:auth-url conf))))

(defn request
  [token]
  (let [request-url (:auth-url conf)
        {body :body request-error :error}
        @(client/request {:url       request-url
                          :method    :get
                          :headers   {"Content-type"  "application/json"
                                      "Authorization" (str "Token " token)}
                          :insecure? true                   ;; TODO: should resolve to make secure SSL work
                          })
        {{group-id :id} :group username :username} (json/read-value body read-mapper)]
    (cond
      (some? request-error) (throw request-error)
      :else {:group-id group-id
             :username username})))
