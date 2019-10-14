(ns data.utils
  (:import (java.util UUID)
           (java.time Instant)))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (int (.getEpochSecond (Instant/now))))

(defn set-dev-aws-system-properties []
  (System/setProperty "aws.region" "eu-central-1")
  (System/setProperty "aws.accessKeyId" "DEV")
  (System/setProperty "aws.secretKey" "DEV"))
