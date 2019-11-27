(ns acc-text.nlg.gf.generator
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.utils :as utils]
            [clojure.string :as str]
            [jsonista.core :as json]))

(defn join-statements [xs]
  (str/join ";\n        " xs))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))

(defn ->abstract [{::grammar/keys [module flags syntax]}]
  (format "abstract %s = {\n    flags\n        %s;\n    cat\n        %s;\n    fun\n        %s;\n}"
          (name module)
          (join-statements
            (map (fn [[flag val]]
                   (format "%s = %s" (name flag) val))
                 flags))
          (join-statements
            (cons (:startcat flags) (mapcat :params syntax)))
          (join-statements
            (map-indexed (fn [i {:keys [name params]}]
                           (format "Function%02d : %s"
                                   (inc i)
                                   (str/join " -> " (-> params (vec) (conj name)))))
                         syntax))))

(defn get-selectors [syntax]
  (let [selectors (->> syntax (map :body) (apply concat) (map :selectors))
        initial-map (zipmap (mapcat keys selectors) (repeat #{}))]
    (apply merge-with conj initial-map selectors)))

(defn parse-params [syntax]
  (map (fn [[k v]]
         (format "%s = %s"
                 (name k)
                 (str/join " | " (sort (map name v)))))
       (get-selectors syntax)))

(defn parse-lincat [syntax]
  (map (fn [[ret functions]]
         (format "%s = {%s: %s}"
                 (str/join ", " (map :name functions))
                 (name (nth ret 0))
                 (nth ret 1)))
       (group-by :ret syntax)))

(defn join-function-body [body]
  (str/join " " (map (fn [{:keys [type value]}]
                       (case type
                         :literal (format "\"%s\"" (escape-string value))
                         :operator value
                         :function (format "%s.s" value)))
                     body)))

(defn parse-lin [syntax]
  (map-indexed (fn [i {:keys [params ret body]}]
                 (format "Function%02d %s= {%s = %s}"
                         (inc i)
                         (str/join (interleave params (repeat " ")))
                         (name (nth ret 0))
                         (if (seq body) (join-function-body body) "\"\"")))
               syntax))

(defn ->concrete [{::grammar/keys [instance module syntax]}]
  (format "concrete %s of %s = {%s\n}"
          (str (name module) (name instance))
          (name module)
          (->> [["param" (parse-params syntax)]
                ["lincat" (parse-lincat syntax)]
                ["lin" (parse-lin syntax)]]
               (filter (comp seq second))
               (map #(format "\n    %s\n        %s;" (first %) (join-statements (second %))))
               (str/join))))

(defn generate [{::grammar/keys [module] :as grammar}]
  (-> (service/compile-request module (->abstract grammar) (->concrete grammar))
      (get :body)
      (json/read-value utils/read-mapper)
      (get-in [:results 0 1])
      (sort)
      (dedupe)))
