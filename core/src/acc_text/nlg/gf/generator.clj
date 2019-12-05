(ns acc-text.nlg.gf.generator
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.utils :as utils]
            [clojure.string :as str]
            [jsonista.core :as json]))

(defn join-body [& args]
  (->> args
       (partition 2)
       (filter (comp seq second))
       (map #(format "\n    %s\n        %s;" (first %) (str/join ";\n        " (second %))))
       (str/join)))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))

(defn parse-flags [flags]
  (map (fn [[flag val]]
         (format "%s = %s" (name flag) val))
       flags))

(defn parse-cat [flags syntax]
  (cons (:startcat flags) (mapcat :params syntax)))

(defn parse-fun [syntax]
  (map-indexed (fn [i {:keys [name params]}]
                 (format "Function%02d : %s"
                         (inc i)
                         (str/join " -> " (-> params (vec) (conj name)))))
               syntax))

(defn get-selectors [syntax]
  (let [selectors (->> syntax (map :body) (apply concat) (map :selectors))
        initial-map (zipmap (mapcat keys selectors) (repeat #{}))]
    (apply merge-with conj initial-map selectors)))

(defn parse-param [syntax]
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

(declare join-function-body)

(defn join-expression [expr]
  (if (sequential? expr)
    (cond->> (join-function-body expr)
             (< 1 (count expr)) (format "(%s)"))
    (let [{:keys [type value]} expr]
      (case type
        :literal (format "\"%s\"" (escape-string value))
        :function (format "%s.s" value)))))

(defn get-operator [expr next-expr]
  (when (some? next-expr)
    (if (some sequential? [expr next-expr])
      "|"
      "++")))

(defn join-function-body [body]
  (str/join " " (map (fn [expr next-expr]
                       (let [operator (get-operator expr next-expr)]
                         (cond-> (join-expression expr)
                                 (some? operator) (str " " operator))))
                     body
                     (concat (rest body) [nil]))))

(defn parse-lin [syntax]
  (map-indexed (fn [i {:keys [params ret body]}]
                 (format "Function%02d %s= {%s = %s}"
                         (inc i)
                         (str/join (interleave params (repeat " ")))
                         (name (nth ret 0))
                         (if (seq body) (join-function-body body) "\"\"")))
               syntax))

(defn ->abstract [{::grammar/keys [module flags syntax]}]
  (format "abstract %s = {%s\n}"
          (name module)
          (join-body
            "flags" (parse-flags flags)
            "cat" (parse-cat flags syntax)
            "fun" (parse-fun syntax))))

(defn ->concrete [{::grammar/keys [instance module syntax]}]
  (format "concrete %s of %s = open %s in {%s\n}"
          (str (name module) (name instance))
          (name module)
          "LangFunctionsEng"
          (join-body
            "param" (parse-param syntax)
            "lincat" (parse-lincat syntax)
            "lin" (parse-lin syntax))))

(defn generate [{::grammar/keys [module] :as grammar}]
  (-> (service/compile-request module (->abstract grammar) (->concrete grammar))
      (get :body)
      (json/read-value utils/read-mapper)
      (get-in [:results 0 1])
      (sort)
      (dedupe)))
