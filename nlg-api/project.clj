(defproject nlg-api "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.amazonaws/aws-lambda-java-core "1.0.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [cheshire "5.8.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [clj-time "0.5.1"]
                 [com.taoensso/faraday "1.9.0"]
                 [uk.ac.abdn/SimpleNLG "4.4.8"]
                 [prismatic/schema "1.1.9"]
                 [org.clojure/data.csv "0.1.4"]
                 [com.amazonaws/aws-java-sdk-s3 "1.10.49"]]
  :plugins [[jonase/eastwood "0.3.3"]]
  :target-path "target/%s"
  :resource-paths ["resources/"]
  :profiles {:uberjar      {:aot [lt.tokenmill.nlg.api.blockly-workspace
                                  lt.tokenmill.nlg.api.data
                                  lt.tokenmill.nlg.api.document-plans
                                  lt.tokenmill.nlg.api.generate
                                  lt.tokenmill.nlg.api.lexicon
                                  lt.tokenmill.nlg.api.resource
                                  lt.tokenmill.nlg.api.utils
                                  lt.tokenmill.nlg.db.config
                                  lt.tokenmill.nlg.db.dynamo-ops
                                  lt.tokenmill.nlg.generator.ops
                                  lt.tokenmill.nlg.generator.parser
                                  lt.tokenmill.nlg.generator.planner
                                  lt.tokenmill.nlg.generator.simple-nlg]}
             :test         {:dependencies []}
             :local-server {:main         local-server
                            :dependencies [[http-kit "2.3.0"]]
                            :aot          [local-server]}}
  :test-selectors {:default     (complement (fn [test] (or (:integration test))))
                   :integration :integration})
