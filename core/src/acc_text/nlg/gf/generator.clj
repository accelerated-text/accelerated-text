(ns acc-text.nlg.gf.generator
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]))

(def dictionary #{"allows" "standard"
                  "this" "small" "of_Prep" "make" "fast" "suitable" "with_Prep"
                  "regular" "features" "easy_N"
                  "includes" "package"})
(defn s-ret? [ret] (coll? ret))

(defn f-param? [function-name] (str/starts-with? function-name "Amr"))

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

(defn join-value [type value]
  (->> value
       (map #(if (contains? dictionary %)
               %
               (case type
                 "Str" (format "\"%s\"" (escape-string %))
                 "Pol" (if (Boolean/valueOf ^String %)
                         "positivePol"
                         "negativePol")
                 "CN" (format "(mkCN (mkN \"%s\"))" (escape-string %))
                 (format "(mk%s \"%s\")" type (escape-string %)))))
       (str/join " | ")))

(defn parse-oper [variables]
  (map (fn [{:keys [name type value]}]
         (cond-> (str name " : " type)
                 (some? value) (str " = " (join-value type value))))
       variables))

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
         (if (s-ret? ret)
           ;;the case when we have [s: Str]
           (format "%s = {%s: %s}"
                   (str/join ", " (map :name functions))
                   (name (nth ret 0))
                   (nth ret 1))
           ;;when ret is some Phrase
           (format "%s = %s"
                   (str/join ", " (map :name functions))
                   ret)))
       (group-by :ret functions)))

(declare join-function-body)

(defn join-expression [expr ret]
  (if (sequential? expr)
    (cond->> (join-function-body expr ret)
             (< 1 (count expr)) (format "(%s)"))
    (let [{:keys [kind value params]} expr]
      (case kind
        :variable value
        :literal (cond->> (format "\"%s\"" (escape-string value))
                          (not= "Str" (second ret)) (format "(mk%s %s)" (second ret)))
        :function (format "%s.s" value)
        :operation (let [flin (->> params
                                   (filter (comp some? :value))
                                   (map (fn [{:keys [kind value]}]
                                          (case kind
                                            :literal (format "\"%s\""
                                                             (escape-string value))
                                            :function
                                            (format "%s%s"
                                                    value
                                                    (if (f-param? value) "" ".s"))
                                            :variable value)))
                                   (str/join " ")
                                   (format "(%s %s)" value))]
                     (str flin (if (s-ret? ret) ".s" "")))))))

(defn get-operator [expr next-expr]
  (when (some? next-expr)
    (if (some sequential? [expr next-expr])
      "|"
      "++")))

(defn join-operation-body [op]
  (->> op
       (map (fn [{:keys [type value children]}]
              (case type
                :argument value
                :operation (format "(%s %s)" value (join-operation-body children))
                :literal (format "\"%s\"" (escape-string value)))))
       (str/join " ")))

(defn join-function-body [body ret]
  (str/join " " (map (fn [expr next-expr]
                       (let [operator (get-operator expr next-expr)]
                         (cond-> (join-expression expr ret)
                                 (some? operator) (str " " operator))))
                     body
                     (concat (rest body) [nil]))))

(defn join-modifier-body [body _]
  (let [concept (:value (last body))
        modifiers (->> (subvec (vec body) 0 (dec (count body)))
                       (map :value)
                       (map #(format "(mkAP %s)" %)))]
    (format "(mkCN %s %s)"
            (cond
              (= 1 (count modifiers)) (first modifiers)
              (= 2 (count modifiers)) (format "(mkAP and_Conj (mkListAP %s %s))" (first modifiers) (second modifiers))
              :else (format
                      "(mkAP and_Conj %s)"
                      (let [modifiers (reverse modifiers)]
                        (loop [[mod & mods] (drop 2 modifiers)
                               body (format "(mkListAP %s %s)" (second modifiers) (first modifiers))]
                          (if-not (some? mod)
                            body
                            (recur mods (format "(mkListAP %s %s)" mod body)))))))
            concept)))

(defn parse-lin [functions]
  (map-indexed (fn [i {:keys [params ret body type]}]
                 (if (s-ret? ret)
                   (format "Function%02d %s= {%s = %s}"
                           (inc i)
                           (str/join (interleave params (repeat " ")))
                           (name (nth ret 0))
                           (if (seq body)
                             (cond
                               (and (= "CN" (second ret)) (= :modifier type)) (join-modifier-body body ret)
                               :else (join-function-body body ret))
                             "\"\""))
                   (format "Function%02d %s= %s"
                           (inc i)
                           (str/join (interleave params (repeat " ")))
                           (if (seq body)
                             (cond
                               (and (= "CN" (second ret)) (= :modifier type)) (join-modifier-body body ret)
                               :else (join-function-body body ret))
                             "\"\""))))
               functions))

(defn ->abstract [{::grammar/keys [module flags functions]}]
  (format "abstract %s = {%s\n}"
          module
          (join-body
            "flags" (parse-flags flags)
            "cat" (parse-cat flags functions)
            "fun" (parse-fun functions))))

(defn ->incomplete [{::grammar/keys [module functions]}]
  (format "incomplete concrete %sBody of %s = open Syntax, %sLex, %sOps, %s in {%s\n}"
          module
          module
          module
          module
          (str/join ", " ["LangFunctionsEng" "CapableOfEng" "MadeOfEng" "HasPropertyEng"
                          "IsAEng" "HasAEng" "AtLocationEng" "LocatedNearEng" "IncludesEng"])
          (join-body
            "param" (parse-param functions)
            "lincat" (parse-lincat functions)
            "lin" (parse-lin functions))))

(defn ->interface [{::grammar/keys [module variables]}]
  (format "interface %sLex = {%s\n}"
          module
          (join-body
            "oper" (parse-oper (map #(dissoc % :value) variables)))))

(defn ->resource [lang {::grammar/keys [module variables]}]
  (format "resource %sLex%s = open Syntax%s, Paradigms%s, BaseDictionaryEng in {%s\n}"
          module
          lang
          lang
          lang
          (join-body
            "oper" (parse-oper variables))))

(defn ->concrete [lang {::grammar/keys [instance module]}]
  (format "concrete %s%s of %s = %sBody with \n  (Syntax=Syntax%s),\n  (%sLex = %sLex%s);"
          module
          instance
          module
          module
          lang
          module
          module
          lang))

(defn ->operations [lang {::grammar/keys [module operations]}]
  (format "resource %sOps = open (Syntax=Syntax%s), Syntax%s, Paradigms%s in {%s\n}"
          module
          lang
          lang
          lang
          (join-body
            "oper" (for [{:keys [id kind roles body]} operations]
                     (if (seq roles)
                       (format
                         "%s : %s = \\%s -> %s"
                         id
                         (str/join " -> " (conj (mapv :type roles) kind))
                         (str/join "," (mapv :id roles))
                         (join-operation-body body))
                       (format
                         "%s : %s = %s"
                         id
                         (str/join " -> " (conj (mapv :type roles) kind))
                         (join-operation-body body)))))))

(defn grammar->content [lang {::grammar/keys [module instance] :as grammar}]
  {(str module)            (->abstract grammar)
   (str module "Body")     (->incomplete grammar)
   (str module "Lex")      (->interface grammar)
   (str module "Lex" lang) (->resource lang grammar)
   (str module "Ops")      (->operations lang grammar)
   (str module instance)   (->concrete lang grammar)})

(defn translate-reader-model [lang]
  (case lang
    :en "Eng"
    :ee "Est"
    :de "Ger"
    :lv "Lav"
    :ru "Rus"))

(defn generate
  ([grammar]
   (generate :en grammar))
  ([lang {::grammar/keys [module instance] :as grammar}]
   (let [lang (translate-reader-model lang)
         {body :body} (service/compile-request lang module instance (grammar->content lang grammar))
         {[[_ results]] :results error :error} (json/read-value body utils/read-mapper)]
     (if (some? error)
       (log/error error)
       (sort (dedupe results))))))

(s/fdef generate
        :args (s/cat :grammar :acc-text.nlg.gf.grammar/grammar :reader-model map?)
        :ret (s/nilable (s/coll-of string?)))
