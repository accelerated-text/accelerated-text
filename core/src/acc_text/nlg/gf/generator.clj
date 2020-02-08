(ns acc-text.nlg.gf.generator
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]))

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
       (map #(case type
               "Str" (format "\"%s\"" (escape-string %))
               "Pol" (if (Boolean/valueOf ^String %)
                       "positivePol"
                       "negativePol")
               "CN" (format "(mkCN (mkN \"%s\"))" (escape-string %))
               (format "(mk%s \"%s\")" type (escape-string %))))
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
         (format "%s = %s"
                 (str/join ", " (map :name functions))
                 (nth ret 1)))
       (group-by :ret functions)))

(declare join-function-body)

(defn join-expression [type expr ret]
  (if (sequential? expr)
    (cond->> (join-function-body type expr ret)
             (< 1 (count expr)) (format "(%s)"))
    (let [{:keys [kind value params]} expr]
      (case kind
        :variable value
        :literal (cond->> (format "\"%s\"" (escape-string value))
                          (not= "Str" (second ret)) (format "(mk%s %s)" (second ret)))
        :function (cond->> value
                           (= "mkAdv" value) (str "ParadigmsEng.")
                           (and (= :segment type)
                                (str/includes? value "Amr")) (format "%s.s"))
        :operation (->> params
                        (filter (comp some? :value))
                        (map (fn [{:keys [kind value]}]
                               (case kind
                                 :literal (format "\"%s\"" (escape-string value))
                                 :function (format "%s" value)
                                 :variable value)))
                        (str/join " ")
                        (format "(%s %s)" value))))))

(defn get-operator [expr next-expr]
  (when (some? next-expr)
    (if (some sequential? [expr next-expr])
      "|"
      "++")))

(defn join-function-body [type body ret]
  (str/join " " (map (fn [expr next-expr]
                       (let [operator (get-operator expr next-expr)]
                         (cond-> (join-expression type expr ret)
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
                 (format "Function%02d %s= %s"
                         (inc i)
                         (str/join (interleave params (repeat " ")))
                         (if (seq body)
                           (cond
                             (and (= "CN" (second ret)) (= :modifier type)) (join-modifier-body body ret)
                             :else (join-function-body type body ret))
                           "\"\"")))
               functions))

(defn ->abstract [{::grammar/keys [module flags functions]}]
  (format "abstract %s = {%s\n}"
          module
          (join-body
            "flags" (parse-flags flags)
            "cat" (parse-cat flags functions)
            "fun" (parse-fun functions))))

(def imports ["ParadigmsEng" "ConstructorsEng" "SyntaxEng"])

(defn ->incomplete [{::grammar/keys [module functions]}]
  (format "incomplete concrete %sBody of %s = open %sLex, %s in {%s\n}"
          module
          module
          module
          (str/join ", " imports)
          (join-body
            "param" (parse-param functions)
            "lincat" (parse-lincat functions)
            "lin" (parse-lin functions))))

(defn ->interface [{::grammar/keys [module variables]}]
  (format "interface %sLex = {%s\n}"
          module
          (join-body
            "oper" (parse-oper (map #(dissoc % :value) variables)))))

(defn ->resource [{::grammar/keys [instance module variables]}]
  (format "resource %sLex%s = open SyntaxEng, ParadigmsEng in {%s\n}"
          module
          instance
          (join-body
            "oper" (parse-oper variables))))

(defn ->concrete [{::grammar/keys [instance module]}]
  (format "concrete %s%s of %s = %sBody with \n  (%sLex = %sLex%s);"
          module
          instance
          module
          module
          module
          module
          instance))

(defn grammar->content [{::grammar/keys [module instance] :as grammar}]
  {(str module)                (->abstract grammar)
   (str module "Body")         (->incomplete grammar)
   (str module "Lex")          (->interface grammar)
   (str module "Lex" instance) (->resource grammar)
   (str module instance)       (->concrete grammar)})

(defn generate [{::grammar/keys [module instance] :as grammar}]
  (let [{body :body} (service/compile-request module instance (grammar->content grammar))
        {[[_ results]] :results error :error} (json/read-value body utils/read-mapper)]
    (if (some? error)
      (log/error error)
      (sort (dedupe results)))))

(s/fdef generate
        :args (s/cat :grammar :acc-text.nlg.gf.grammar/grammar)
        :ret (s/nilable (s/coll-of string?)))
