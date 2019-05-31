(ns graphql.server
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as cheshire]
            [graphql.core :as graphql]
            [org.httpkit.server :as server]
            [clojure.string :as str]))

(defonce server (atom nil))

(defn http-result [body]
  {:status  200
   :headers {"Access-Control-Allow-Origin"  "*"
             "Access-Control-Allow-Headers" "content-type, *"
             "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
             "Content-Type"                 "application/json"}
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

(defn app [{:keys [body uri request-method] :as request}]
  (if (= request-method :options)
    {:status  200
     :headers {"Access-Control-Allow-Origin"  "*"
               "Access-Control-Allow-Headers" "content-type, *"
               "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}}
    (if (= "/_graphql" (str/lower-case uri))
      (http-result (graphql/nlg (normalize-body body)))
      {:status 404
       :body   (format "ERROR: unsupported URI '%s'" uri)})))

(defn -main [& args]
  (let [host (or (System/getenv "HOST") "0.0.0.0")
        port (Integer/valueOf ^String (or (System/getenv "PORT") "3001"))]
    (log/infof "Running server on: localhost:%s. Press Ctrl+C to stop" port)
    (reset! server (server/run-server #'app {:port port :ip host}))))
