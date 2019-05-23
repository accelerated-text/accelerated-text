(ns graphql.server
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as cheshire]
            [graphql.core :as graphql]
            [org.httpkit.server :as server]))

(defonce server (atom nil))

(defn http-result [body]
  (log/infof "Current body '%s'" body)
  {:statusCode      200
   :isBase64Encoded false
   :headers         {"Access-Control-Allow-Origin"  "*"
                     "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}
   :body            (cheshire.core/encode body)})

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn request-body [{:keys [body]}]
  (-> body
      (.bytes)
      (String.)
      (cheshire/decode true)))

(defn app [req]
  (http-result (graphql/nlg (request-body req))))

(defn -main [& args]
  (log/infof "Running server on: localhost:3001. Press Ctrl+C to stop")
  (reset! server (server/run-server app {:port 3001})))
