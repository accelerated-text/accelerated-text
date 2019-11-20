(ns acc-text.nlg.gf.grammar.gf
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [clojure.string :as string]))


(defn wrap-abstract [name body]
  (format "abstract %s = {\n%s\n}" name (string/join "\n" body)))

(defn wrap-concrete [name of body]
  (format "concrete %s of %s = {\n%s\n}" name of (string/join "\n" body)))

(defn abstract->gf [{module-name :acc-text.nlg.gf.grammar/module-name
                     flags       :acc-text.nlg.gf.grammar/flags
                     categories  :acc-text.nlg.gf.grammar/categories
                     functions   :acc-text.nlg.gf.grammar/functions}]
  (wrap-abstract module-name (list
                              (format "  flags\n    %s;" (string/join ", " (map (fn [[label category]] (format "%s = %s" (name label) category)) flags)))
                              (format "  cat\n    %s;" (string/join "; " categories))
                              (format "  fun\n    %s;" (string/join ";\n    " (for [{name      :acc-text.nlg.gf.grammar/name
                                                                                   arguments :acc-text.nlg.gf.grammar/arguments
                                                                                   return    :acc-text.nlg.gf.grammar/return} functions]
                                                                              (if (seq arguments)
                                                                                (format "%s : %s -> %s" name (string/join " -> " arguments) return)
                                                                                (format "%s : %s" name return))))))))


(defn lin-function->gf [{name   :acc-text.nlg.gf.grammar/function-name
                         syntax :acc-text.nlg.gf.grammar/syntax}]
  (let [resolve-role  (fn [{role  :acc-text.nlg.gf.grammar/role
                            value :acc-text.nlg.gf.grammar/value}]
                        (case role
                          :literal (format "\"%s\"" value)
                          :operation value
                          :function (format "%s.s" value)))
        category-args (->> (filter #(= (:acc-text.nlg.gf.grammar/role %) :function) syntax)
                           (map :acc-text.nlg.gf.grammar/value)
                           (string/join " "))

        function-definition (if (empty? category-args)
                              name
                              (format "%s %s" name category-args))]
    (format "%s = {s = %s};" function-definition (->> syntax
                                                      (map resolve-role)
                                                      (string/join " ")))))


(defn concrete->gf [{module-name :acc-text.nlg.gf.grammar/module-name
                     of          :acc-text.nlg.gf.grammar/of
                     lin-types   :acc-text.nlg.gf.grammar/lin-types
                     lins        :acc-text.nlg.gf.grammar/lins}]
  (wrap-concrete module-name of (list
                                 (format "  lincat\n    %s" (string/join "\n    " (map (fn [[category [t T]]] (format "%s = {%s : %s};" (name category) (name t) (name T))) lin-types)))
                                 (format "  lin\n    %s" (string/join "\n    " (map lin-function->gf lins)))
                                 )))
