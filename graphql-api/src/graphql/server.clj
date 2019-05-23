(ns graphql.server
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as cheshire]
            [graphql.core :as graphql]
            [org.httpkit.server :as server]
            [clojure.string :as str]))

(defonce server (atom nil))

(defn http-result [body]
  {:status  200
   :headers {"Access-Control-Allow-Origin"  "*"
             "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}
   :body    (cheshire.core/encode body)})

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn normalize-body [body]
  (-> body
      (.bytes)
      (String.)
      (cheshire/decode true)))

(defn app [{:keys [body uri]}]
  (log/infof "URI ---->>>> %s" uri)
  (if (= "/_graphql" (str/lower-case uri))
    (http-result (graphql/nlg (normalize-body body)))
    {:status 404
     :body   (format "ERROR: unsupported URI '%s'" uri)}))

(defn -main [& args]
  (log/infof "Running server on: localhost:3001. Press Ctrl+C to stop")
  (reset! server (server/run-server app {:port 3001})))
