(ns acc-text.nlg.generator
  (:require [acc-text.nlg.grammar :as grammar]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn join-body [& args]
  (->> args
       (partition 2)
       (filter (comp seq second))
       (map #(format "\n    %s\n        %s ;" (first %) (str/join " ;\n        " (second %))))
       (str/join)))

(defn ->abstract [{::grammar/keys [module flags cat fun]}]
  (format "abstract %s = {%s\n}"
          module
          (join-body
            "flags" (map (fn [[flag val]]
                           (format "%s = %s" (name flag) val))
                         flags)
            "cat" cat
            "fun" (map (fn [index c]
                         (let [funs (get fun c)]
                           (if (seq funs)
                             (format "Function%02d : %s -> %s" index (str/join " -> " (get fun c)) c)
                             (format "Function%02d : %s" index c))))
                       (rest (range))
                       cat))))

(defn ->incomplete [lang {::grammar/keys [module cat fun lincat lin]}]
  (format "incomplete concrete %sBody of %s = open Syntax, Grammar, %sLex, Paradigms%s in {%s\n}"
          module
          module
          module
          lang
          (join-body
            "lincat" (->> lincat
                          (reduce-kv (fn [m c type]
                                       (update m type #(conj % c)))
                                     {})
                          (map (fn [[type cs]]
                                 (format "%s = %s" (str/join ", " cs) type))))
            "lin" (map (fn [index c]
                         (let [funs (get fun c)
                               body (remove str/blank? (get lin c))
                               l (str/join " | " (cond-> body (empty? body) (conj "\"\"")))]
                           (if (seq funs)
                             (format "Function%02d %s = %s" index (str/join " " funs) l)
                             (format "Function%02d = %s" index l))))
                       (rest (range))
                       cat))))

(defn ->interface [{::grammar/keys [module oper]}]
  (format "interface %sLex = {%s\n}"
          module
          (join-body
            "oper" (map (fn [[c type _]]
                          (format "%s : %s" c type))
                        oper))))

(defn ->resource [lang {::grammar/keys [module oper]}]
  (format "resource %sLex%s = open Syntax%s, Grammar%s, Paradigms%s, Morpho%s in {%s\n}"
          module
          lang
          lang
          lang
          lang
          (if (= lang "Lav") "Eng" lang)
          (join-body
            "oper" (map (fn [[c type body]]
                          (format "%s : %s = %s" c type body))
                        oper))))

(defn ->concrete [lang {::grammar/keys [instance module]}]
  (format "concrete %s%s of %s = %sBody with \n  (Syntax=Syntax%s),\n  (Grammar=Grammar%s),\n  (%sLex=%sLex%s);"
          module
          instance
          module
          module
          lang
          lang
          module
          module
          lang))

(defn generate [{::grammar/keys [module instance] :as grammar} lang]
  {(str module)            (->abstract grammar)
   (str module "Body")     (->incomplete lang grammar)
   (str module "Lex")      (->interface grammar)
   (str module "Lex" lang) (->resource lang grammar)
   (str module instance)   (->concrete lang grammar)})

(s/fdef generate
        :args (s/cat :grammar ::grammar/grammar :language string?)
        :ret map?)
