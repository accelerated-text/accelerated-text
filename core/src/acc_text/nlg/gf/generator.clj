(ns acc-text.nlg.gf.generator
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [jsonista.core :as json]))

(defn join-body [& args]
  (->> args
       (partition 2)
       (filter (comp seq second))
       (map #(format "\n    %s\n        %s ;" (first %) (str/join " ;\n        " (second %))))
       (str/join)))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))

(defn parse-flags [flags]
  (map (fn [[flag val]]
         (format "%s = %s" (name flag) val))
       flags))

(defn parse-cat [flags functions]
  (cons (:startcat flags) (mapcat :params functions)))

(defn parse-fun [functions]
  (map-indexed (fn [i {:keys [name params]}]
                 (format "Function%02d : %s"
                         (inc i)
                         (str/join " -> " (-> params (vec) (conj name)))))
               functions))

(defn get-selectors [functions]
  (let [selectors (->> functions (map :body) (apply concat) (map :selectors))
        initial-map (zipmap (mapcat keys selectors) (repeat #{}))]
    (apply merge-with conj initial-map selectors)))

(defn parse-param [functions]
  (map (fn [[k v]]
         (format "%s = %s"
                 (name k)
                 (str/join " | " (sort (map name v)))))
       (get-selectors functions)))

(defn parse-lincat [functions]
  (map (fn [[ret functions]]
         (format "%s = {%s: %s}"
                 (str/join ", " (map :name functions))
                 (name (nth ret 0))
                 (nth ret 1)))
       (group-by :ret functions)))

(declare join-function-body)

(defn join-expression [expr ret]
  (if (sequential? expr)
    (cond->> (join-function-body expr ret)
             (< 1 (count expr)) (format "(%s)"))
    (let [{:keys [type value params]} expr]
      (case type
        :variable value
        :literal (cond->> (format "\"%s\"" (escape-string value))
                          (not= "Str" (second ret)) (format "(mk%s %s)" (second ret)))
        :function (format "%s.s" value)
        :operation (->> params
                        (filter (comp some? :value))
                        (map (fn [{:keys [type value]}]
                               (case type
                                 :literal (format "\"%s\"" (escape-string value))
                                 :function (format "%s.s" value)
                                 :variable value)))
                        (str/join " ")
                        (format "(%s %s).s" value))))))

(defn get-operator [expr next-expr]
  (when (some? next-expr)
    (if (some sequential? [expr next-expr])
      "|"
      "++")))

(defn join-function-body [body ret]
  (str/join " " (map (fn [expr next-expr]
                       (let [operator (get-operator expr next-expr)]
                         (cond-> (join-expression expr ret)
                                 (some? operator) (str " " operator))))
                     body
                     (concat (rest body) [nil]))))

(defn join-value [type value]
  (str/join " | " (if (not= type "Str")
                    (map #(format "mk%s \"%s\"" type (escape-string %)) value)
                    (map #(format "\"%s\"" (escape-string %)) value))))

(defn parse-lin [functions]
  (map-indexed (fn [i {:keys [params ret body]}]
                 (format "Function%02d %s= {%s = %s}"
                         (inc i)
                         (str/join (interleave params (repeat " ")))
                         (name (nth ret 0))
                         (if (seq body) (join-function-body body ret) "\"\"")))
               functions))

(defn ->abstract [{::grammar/keys [module flags functions]}]
  (format "abstract %s = {%s\n}"
          module
          (join-body
            "flags" (parse-flags flags)
            "cat" (parse-cat flags functions)
            "fun" (parse-fun functions))))

(defn ->incomplete [{::grammar/keys [instance module functions]}]
  (format "incomplete concrete %sBody of %s = open %sLex, %s in {%s\n}"
          module
          module
          module
          "LangFunctionsEng, ConceptNetEng, SyntaxEng, ParadigmsEng"
          (join-body
            "param" (parse-param functions)
            "lincat" (parse-lincat functions)
            "lin" (parse-lin functions))))

(defn ->interface [{::grammar/keys [instance module variables]}]
  (format "interface %sLex = {%s\n}"
          module
          (if (seq variables)
            (format "\n  oper\n    %s ;"
                    (->> variables
                         (map (fn [{:keys [name type]}]
                                (str name " : " type)))
                         (str/join " ;\n    ")))
            "")))

(defn ->resource [{::grammar/keys [instance module variables]}]
  (format "resource %sLex%s = open SyntaxEng, ParadigmsEng in {%s\n}"
          module
          instance
          (if (seq variables)
            (format "\n  oper\n    %s ;"
                    (->> variables
                         (map (fn [{:keys [name type value]}]
                                (str name " : " type " = " (join-value type value))))
                         (str/join " ;\n    ")))
            "")))

(defn ->concrete [{::grammar/keys [instance module]}]
  (format "concrete %s%s of %s = %sBody with \n  (%sLex = %sLex%s);"
          module
          instance
          module
          module
          module
          module
          instance))

(defn generate [{::grammar/keys [module instance] :as grammar}]
  (-> (service/compile-request module instance {(str module)                (->abstract grammar)
                                                (str module "Body")         (->incomplete grammar)
                                                (str module "Lex")          (->interface grammar)
                                                (str module "Lex" instance) (->resource grammar)
                                                (str module instance)       (->concrete grammar)})
      (get :body)
      (json/read-value utils/read-mapper)
      (get-in [:results 0 1])
      (sort)
      (dedupe)))

(s/fdef generate
        :args (s/cat :grammar :acc-text.nlg.gf.grammar/grammar)
        :ret (s/coll-of string?))
