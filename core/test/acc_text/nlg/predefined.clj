(ns acc-text.nlg.predefined
  (:require [acc-text.nlg.grammar :as g]
            [acc-text.nlg.dsl.core :as dsl]
            [acc-text.nlg.grammar-generation.translate :as translate]))

(def types (g/build-types (list {:name "sem-obj"}
                                {:name "phys-obj" :parents "sem-obj"}
                                {:name "garment" :parents "phys-obj"}
                                {:name "shoe" :parents "garment"}
                                {:name "redesigned-shoe" :parents "shoe"}
                                {:name "purpose" :parents "sem-obj"})))

(def rules (g/build-default-rules))

(def families
  (list
   (dsl/family "Product" :NNP false
               (dsl/entry
                "Primary"
                (dsl/lf "P:shoe")
                (dsl/atomcat :NNP {:index 2}
                             (dsl/fs-nomvar "index" "P")
                             (dsl/fs-featvar "design" "DESIGN:garment"))))

   (dsl/family "Benefits" :NP false
               (dsl/entry
                "Primary"
                (dsl/lf "B:benefit")
                (dsl/atomcat :NP {:index 3} (dsl/fs-nomvar "index" "B"))))

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

   (dsl/family "coord.objects" :Conj true
               (dsl/entry
                "NP-Collective"
                "and"
                (dsl/lf "X0" "and"
                        (dsl/diamond "First"
                                     {:nomvar "L1"
                                      :prop "elem"
                                      :diamonds (list
                                                 (dsl/diamond "Item" {:nomvar "X1"})
                                                 (dsl/diamond "Next" {:nomvar "L2"
                                                                      :prop "elem"
                                                                      :diamond (dsl/diamond "Item" {:nomvar "X2"})}))}))
                (dsl/>F
                 \.
                 (dsl/<B
                  (dsl/atomcat :NP {}
                               (dsl/fs-feat "num" "pl")
                               (dsl/fs-nomvar "index" "X0"))
                  (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X1")))
                 (dsl/atomcat :NP {} (dsl/fs-nomvar "index" "X2")))))

   (dsl/family "adj.redesign" :ADJ true
               (dsl/entry
                "adj.full"
                (dsl/lf "U:shoe-modifier" nil
                        (dsl/diamond "concrete" {:prop "true"})
                        (dsl/diamond "domain" {:nomvar "T"})
                        (dsl/diamond "attribute" {:prop "[*DEFAULT*]"
                                                  :nomvar "P:redesign"
                                                  :diamond (dsl/diamond "concrete" {:prop "true"})}))
                (dsl/>F
                 (dsl/atomcat :NNP {:inherits-from 1} (dsl/fs-nomvar "index" "T"))
                 (dsl/atomcat :NNP {:index 6}
                              (dsl/fs-feat "design" "redesign")
                              (dsl/fs-nomvar "index" "T"))))
               (dsl/member "redesign"))

  ;; adj.benefit and adj.redesign are being resolved from `redesigned` word
   (dsl/family "adj.benefit" :ADJ true
               (dsl/entry
                "adj.full"
                (dsl/lf "U" nil
                        (dsl/diamond "concrete" {:prop "true"})
                        (dsl/diamond "gum-domain" {:nomvar "T"})
                        (dsl/diamond "gum-attribute" {:prop "[*DEFAULT*]"
                                                      :nomvar "P:gum-Color"
                                                      :diamond (dsl/diamond "concrete" {:prop "true"})}))
                (dsl/>F
                 (dsl/atomcat :NP {:inherits-from 1} (dsl/fs-nomvar "index" "T"))
                 (dsl/atomcat :NP {:index 7} (dsl/fs-nomvar "index" "T"))))
               (dsl/member "durable")
               (dsl/member "lasting"))))

(def morph (list
            (dsl/morph-entry "provides" :V {:stem "benefit" :class "purpose"})
            (dsl/morph-entry "offers" :V {:stem "benefit" :class "purpose"})
            (dsl/morph-entry "gives" :V {:stem "benefit" :class "purpose"})

            (dsl/morph-entry "Nike1" :NNP {:stem "nike1" :class "shoe" :macros "@new"})
            (dsl/morph-entry "Nike2" :NNP {:stem "nike2" :class "shoe" :macros "@new"})
            (dsl/morph-entry "Nike3" :NNP {:stem "nike3" :class "shoe" :macros "@new"})
            (dsl/morph-entry "Nike4" :NNP {:stem "nike4" :class "redesigned-shoe" :macros "@redesign"})

            (dsl/morph-entry "durable" :ADJ {})
            (dsl/morph-entry "lasting" :ADJ {})

            (dsl/morph-entry "support" :NP {:class "benefit"})
            (dsl/morph-entry "comfort" :NP {:class "benefit"})

            (dsl/morph-entry "redesigned" :ADJ {:stem "redesign"})

            (dsl/morph-entry "and" :Conj {:stem "and"})
            (dsl/morph-entry "," :Conj {:stem "and"})))

(def macros (list
             (dsl/macro "@redesign" (dsl/fs "redesign" "design" 2))
             (dsl/macro "@new" (dsl/fs "new" "design" 2))))

(defn custom-build-grammar
  []
  (let [grammar-builder (g/build-grammar {:types types :rules rules})
        lexicon         (g/build-lexicon
                         {:families (map translate/family->entry families)
                          :morph    (map translate/morph->entry morph)
                          :macros   (map translate/macro->entry macros)})]
    (grammar-builder lexicon)))
