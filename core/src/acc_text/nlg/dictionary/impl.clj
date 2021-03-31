(ns acc-text.nlg.dictionary.impl
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.lang.eng :as eng]
            [acc-text.nlg.dictionary.lang.other :as other]
            [acc-text.nlg.gf.operations :as operations]))

(defmulti resolve-dict-item ::dict-item/language)

(defmethod resolve-dict-item "Eng" [dict-item]
  (eng/resolve-dict-item dict-item))

(defmethod resolve-dict-item :default [dict-item]
  (other/resolve-dict-item dict-item))

(defn resolve-operation [{language ::dict-item/language {operation "Operation"} ::dict-item/attributes}]
  (when-let [{:keys [name module] :or {module "Syntax"}} (get operations/operation-map operation)]
    (format "%s%s.%s" module language name)))
