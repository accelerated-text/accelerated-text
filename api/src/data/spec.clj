(ns data.spec
  (:require [clojure.spec.alpha :as s]
            [data.spec.reader-model :as reader-model]
            [data.spec.result :as result]))

(s/def ::result (s/keys :req [::result/id ::result/status]
                        :opt [::result/rows ::result/error-message]))

(s/def ::results (s/coll-of ::result))

(s/def ::reader-model (s/coll-of (s/keys :req [::reader-model/code
                                               ::reader-model/name
                                               ::reader-model/type
                                               ::reader-model/enabled?]
                                         :opt [::reader-model/flag
                                               ::reader-model/available?])))
