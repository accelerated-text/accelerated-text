(ns acc-text.nlg.ccg.base-en
  (:require [acc-text.nlg.dsl.core :as dsl]))

(def initial-families
  [;; AND rule. Example: <word1> and <word2>
   (dsl/family "coord.objects" :Conj true
               (dsl/entry
                 "NP-Collective"
                 "and"
                 (dsl/lf "X0" "and"
                         (dsl/diamond "First"
                                      {:nomvar   "L1"
                                       :prop     "elem"
                                       :diamonds [(dsl/diamond "Item" {:nomvar "X1"})
                                                  (dsl/diamond "Next" {:nomvar  "L2"
                                                                       :prop    "elem"
                                                                       :diamond (dsl/diamond "Item" {:nomvar "X2"})})]}))
                 (dsl/>F
                   \.
                   (dsl/<B
                     (dsl/atomcat :NP {}
                                  (dsl/fs-feat "num" "pl")
                                  (dsl/fs-nomvar "index" "X0"))
                     (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X1")))
                   (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X2")))))
   ;; PROVIDES rule. Example: <word1> provides <word2>
   (dsl/family "v.provide" :V false
               (dsl/entry
                 "Primary"
                 (dsl/lf "E" "[*DEFAULT*]"
                         (dsl/diamond "Thing" {:nomvar "X"})
                         (dsl/diamond "Benefit" {:nomvar "Y"}))
                 (dsl/>F
                   \>
                   (dsl/<B
                     (dsl/atomcat :S {} (dsl/fs-nomvar "index" "E"))
                     (dsl/atomcat :NNP {} (dsl/fs-nomvar "index" "X")))
                   (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "Y")))))
   ;; Full consequence rule. <word1> results in <word2>
   (dsl/family "cons2full" :Vp false
               (dsl/entry
                 "Primary"
                 (dsl/lf "H")
                 (dsl/>F
                   (dsl/<B
                     (dsl/atomcat :S {:index "12"} (dsl/fs-nomvar "index" "X0"))
                     (dsl/>F
                       (dsl/atomcat :NNP {} (dsl/fs-nomvar "index" "X1"))
                       (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X3"))))
                   (dsl/atomcat :IN {} (dsl/fs-nomvar "index" "X2")))))
   (dsl/family "IN" :IN false
               (dsl/entry
                 "IN"
                 "in"
                 (dsl/lf "I")
                 (dsl/atomcat :IN {} (dsl/fs-nomvar "index" "I"))))

   (dsl/family "Modifier" :ADJ false
               (dsl/entry
                 "Primary"
                 (dsl/lf "E" "[*DEFAULT*]"
                         (dsl/diamond "Modifier" {:nomvar "X"})
                         (dsl/diamond "Thing" {:nomvar "Y"}))
                 (dsl/>F
                   (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X"))
                   (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "Y")))))])

(def initial-morph
  [(dsl/morph-entry "provides" :V {:stem "benefit" :class "purpose"})
   (dsl/morph-entry "offers" :V {:stem "benefit" :class "purpose"})
   (dsl/morph-entry "gives" :V {:stem "benefit" :class "purpose"})
   (dsl/morph-entry "results" :Vp {:stem "consequence" :class "consequence"})
   (dsl/morph-entry "in" :IN {:stem "in"})])
