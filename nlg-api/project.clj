(defproject nlg-api "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.amazonaws/aws-lambda-java-core "1.0.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [cheshire "5.8.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [clj-time "0.5.1"]
                 [com.taoensso/faraday "1.9.0"] ;; DynamoDB client
                 [uk.ac.abdn/SimpleNLG "4.4.8"] ;; SimpleNLG
                 [prismatic/schema "1.1.9"]
                 [org.clojure/data.csv "0.1.4"]
                 [com.amazonaws/aws-java-sdk-s3 "1.11.517"] ;; S3 Access
]
  :plugins [[jonase/eastwood "0.3.3"]]
  :target-path "target/%s"
  :resource-paths ["resources/"]
  :profiles {:uberjar {:aot :all}})
