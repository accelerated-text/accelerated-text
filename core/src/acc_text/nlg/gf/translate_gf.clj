(ns acc-text.nlg.gf.translate-gf
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [clojure.string :as string]))


(defn wrap-abstract [name body]
  (format "abstract %s = {\n%s\n}" name (string/join ";\n" body)))

(defn wrap-concrete [name of body]
  (format "concrete %s of %s = {\n%s\n}" name of (string/join ";\n" body)))

(defn abstract->gf [{module-name :acc-text.nlg.gf.grammar/module-name
                     flags       :acc-text.nlg.gf.grammar/flags
                     categories  :acc-text.nlg.gf.grammar/categories
                     functions   :acc-text.nlg.gf.grammar/functions}]
  (wrap-abstract module-name (list
                              (format "  flags\n    %s" (string/join ", " (map (fn [[label category]] (format "%s = %s" (name label) category)) flags)))
                              (format "  cat\n    %s" (string/join "; " categories))
                              (format "  fun\n    %s" (string/join "\n    " (for [{name      :acc-text.nlg.gf.grammar/name
                                                                                   arguments :acc-text.nlg.gf.grammar/arguments
                                                                                   return    :acc-text.nlg.gf.grammar/return} functions]
                                                                              (if (seq arguments)
                                                                                (format "%s : %s -> %s;" name (string/join " -> " arguments) return)
                                                                                (format "%s : %s;" name return))))))))

;; concrete GoodBookEng of GoodBook = {
;;   lincat
;;     Sentence, Data, Modifier = {s : Str};

;;   lin
;;     GoodTitle m d = {s = m.s ++ d.s};
;;     DataTitle = {s = "{{TITLE}}"};
;;     GoodModifier = {s = "good" | "nice"};
;; }


(defn concrete->gf [{module-name :acc-text.nlg.gf.grammar/module-name
                     of          :acc-text.nlg.gf.grammar/of
                     lin-types   :acc-text.nlg.gf.grammar/lin-types}]
  (wrap-concrete module-name of (list
                                 (format "  lincat\n    %s" (string/join "\n    " (map (fn [[category [t T]]] (format "%s = {%s : %s};" (name category) (name t) (name T))) lin-types)))
                                 )))
