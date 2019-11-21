(ns acc-text.nlg.gf.grammar.gf
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [clojure.string :as string]))

(defn gf-name [n]
  (->> (string/split (name n) #"-")
       (map string/capitalize)
       (string/join "")))

(defn wrap-abstract [name body]
  (format "abstract %s = {\n%s\n}" (gf-name name) (string/join "\n" body)))

(defn wrap-concrete [name of body]
  (format "concrete %s of %s = {\n%s\n}" (gf-name name) (gf-name of) (string/join "\n" body)))

(defn abstract->gf [{module-name ::grammar/module-name
                     flags       ::grammar/flags
                     categories  ::grammar/categories
                     functions   ::grammar/functions}]
  (wrap-abstract module-name [(format "  flags\n    %s;"
                                      (string/join ", " (map (fn [[label category]] (format "%s = %s" (name label) (gf-name category))) flags)))
                              (format "  cat\n    %s;" (string/join "; " (map gf-name categories)))
                              (format "  fun\n    %s;"
                                      (string/join ";\n    "
                                                   (for [{fn-name      ::grammar/function-name
                                                          arguments ::grammar/arguments
                                                          return    ::grammar/return} functions]
                                                     (if (seq arguments)
                                                       (format "%s : %s -> %s" (gf-name fn-name) (string/join " -> " (map gf-name arguments)) (gf-name return))
                                                       (format "%s : %s" (gf-name fn-name) (gf-name return))))))]))

(defn lin-function->gf [{name ::grammar/function-name syntax ::grammar/syntax}]
  (let [role-vars     (into {}
                            (map-indexed (fn [idx v]
                                           [v (format "x%d" idx)])
                                         (->> syntax
                                              (filter #(= (::grammar/role %) :function))
                                              (map ::grammar/value))))
        resolve-role  (fn [{role ::grammar/role value ::grammar/value}]
                        (case role
                          :literal (format "\"%s\"" value)
                          :operation value
                          :function (format "%s.s" (get role-vars value))))
        category-args (->> syntax
                           (filter #(= (::grammar/role %) :function))
                           (map ::grammar/value)
                           (map #(get role-vars %))
                           (string/join " "))
        function-definition (if (empty? category-args)
                              (gf-name name)
                              (format "%s %s" (gf-name name) category-args))]
    (format "%s = {s = %s};" function-definition (->> syntax
                                                      (map resolve-role)
                                                      (string/join " ")))))

(defn concrete->gf [{module-name ::grammar/module-name
                     of          ::grammar/of
                     lin-types   ::grammar/lin-types
                     lins        ::grammar/lins}]
  (wrap-concrete module-name of [(format "  lincat\n    %s"
                                         (string/join "\n    "
                                                      (map (fn [[category [t T]]]
                                                             (format "%s = {%s : %s};" (gf-name category) (name t) (gf-name T)))
                                                           lin-types)))
                                 (format "  lin\n    %s"
                                         (string/join "\n    " (map lin-function->gf lins)))]))
