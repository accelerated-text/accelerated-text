(ns acc-text.nlg.gf.generator
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn ->abstract [{::grammar/keys [module flags syntax]}]
  (format "abstract %s = {\n    flags\n        %s;\n    cat\n        %s;\n    fun\n        %s;\n}"
          (name module)
          (->> flags
               (map (fn [[flag val]]
                      (format "%s = %s" (name flag) val)))
               (str/join ";\n        "))
          (->> syntax
               (mapcat :params)
               (cons (:startcat flags))
               (str/join ";\n        "))
          (->> syntax
               (map-indexed (fn [i {:keys [name params]}]
                              (format "Function%02d : %s"
                                      (inc i)
                                      (str/join " -> " (-> params (vec) (conj name))))))
               (str/join ";\n        "))))

(defn ->concrete [{::grammar/keys [instance module syntax]}]
  (format "concrete %s of %s = {\n    lincat\n        %s;\n    lin\n        %s;\n}"
          (name instance)
          (name module)
          (->> syntax
               (group-by :ret)
               (map (fn [[ret functions]]
                      (format "%s = {%s: %s}"
                              (str/join ", " (map :name functions))
                              (name (nth ret 0))
                              (nth ret 1))))
               (str/join ";\n        "))
          (->> syntax
               (map-indexed (fn [i {:keys [params ret body]}]
                              (format "Function%02d %s= {%s = %s}"
                                      (inc i)
                                      (str/join (interleave params (repeat " ")))
                                      (name (nth ret 0))
                                      (if-not (seq body)
                                        "\"\""
                                        (->> body
                                             (map (fn [{:keys [type value]}]
                                                    (case type
                                                      :literal (format "\"%s\"" (str/replace value #"\"" "\\\\\""))
                                                      :operator value
                                                      :function (format "%s.s" value))))
                                             (str/join " "))))))
               (str/join ";\n        "))))

(defn compile-request [{::grammar/keys [module] :as grammar}]
  (let [request-url (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
        abstract-grammar (->abstract grammar)
        concrete-grammar (->concrete grammar)
        request-content {:name     (name module)
                         :abstract {:content abstract-grammar}
                         :concrete [{:key     1
                                     :content concrete-grammar}]}]
    (log/debugf "Compiling grammar via %s:\n%s\n%s" request-url abstract-grammar concrete-grammar)
    (log/tracef "Request:\n curl -X POST -H \"Content-Type: application/json\"  %s -d '%s'"
                request-url (json/write-value-as-string request-content))
    @(client/request {:url     request-url
                      :method  :post
                      :headers {"Content-type" "application/json"}
                      :body    (json/write-value-as-string request-content)})))

(defn generate [grammar]
  (-> grammar
      (compile-request)
      (get :body)
      (json/read-value read-mapper)
      (get-in [:results 0 1])
      (sort)
      (dedupe)))
