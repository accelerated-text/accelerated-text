(ns acc-text.nlg.grammar-generation.translate-test
  (:require [clojure.test :refer [testing is deftest]]
            [acc-text.nlg.grammar-generation.translate :as tr]
            [acc-text.nlg.dsl.core :as dsl]))

(defn attribute->dict [attr]
  {:name (.getName attr)
   :value (.getValue attr)
   :ns (.getNamespaceURI attr)})

(defn compare-elements [e1 e2]
  (testing (format "Comparing %s vs %s\n>" e1 e2)
    (is (= (.getName e1) (.getName e2)))
    (is (= (map attribute->dict (.getAttributes e1))
           (map attribute->dict (.getAttributes e2))))
    (let [c1 (.getChildren e1)
          c2 (.getChildren e2)]
      (is (= (count c1) (count c2)) (format "%s\nvs\n%s" c1 c2))
      (doseq [pair (map vector c1 c2)]
        (apply compare-elements pair)))))

(deftest translate-morph
  (testing "Product -> morph"
    (let [result (tr/morph->entry
                  (dsl/morph-entry "Nike1" :NNP {:stem "nike1"
                                                 :class "shoe"
                                                 :macros "@new"}))
          expected (tr/build-morph-entry {:pos :NNP :word "Nike1" :stem "nike1"
                                          :class "shoe" :macros "@new"})]
      (compare-elements expected result))))

(deftest translate-macro
  (testing "@new macro"
    (let [result (tr/macro->entry
                  (dsl/macro "@new" (dsl/fs "new" "design" 2)))
          expected (tr/build-macro "@new" (tr/build-fs {:val "new" :attr "design" :id "2"}))]
      (compare-elements expected result))))

(deftest translate-diamond
  (testing "complex translation"
    (let [result (tr/diamond->entry
                  (dsl/diamond "gum-attribute"
                               {:nomvar "P:gum-Color"
                                :prop "[*DEFAULT*]"
                                :diamond (dsl/diamond "concrete" {:prop "true"})}))
          expected (tr/build-diamond
                    "gum-attribute"
                    (tr/build-nomvar "P:gum-Color")
                    (tr/build-prop "[*DEFAULT*]")
                    (tr/build-diamond "concrete" (tr/build-prop "true")))]
      (compare-elements expected result))))

(deftest translate-lf
  (testing "simple lf"
    (let [result (tr/logical-form->entry (dsl/lf "P:shoe"))
          expected (tr/build-lf
                    (tr/build-satop "P:shoe" (tr/build-prop "[*DEFAULT*]")))]
      (compare-elements expected result)))
  (testing "nested lf"
    (let [result (tr/logical-form->entry (dsl/lf "U" nil
                                              (dsl/diamond "concrete" {:prop "true"})
                                              (dsl/diamond "gum-domain" {:nomvar "T"})
                                              (dsl/diamond "gum-attribute"
                                                           {:prop "[*DEFAULT*]"
                                                            :nomvar "P:gum-Color"
                                                            :diamond (dsl/diamond "concrete" {:prop "true"})})))
          expected (tr/build-lf
                    (tr/build-satop "U"
                                    (tr/build-diamond "concrete"
                                                      (tr/build-prop "true"))
                                    (tr/build-diamond "gum-domain"
                                                (tr/build-nomvar "T"))
                                    (tr/build-diamond "gum-attribute"
                                                      (tr/build-nomvar "P:gum-Color")
                                                      (tr/build-prop "[*DEFAULT*]")
                                                      (tr/build-diamond "concrete"
                                                                        (tr/build-prop "true")))))]
      (compare-elements expected result))))


(deftest translate-atomcat
  (let [result (tr/category->entry
                (dsl/atomcat "nnp" {:index 2}
                             (dsl/fs-nomvar "index" "P")
                             (dsl/fs-featvar "design" "DESIGN:garment"))
                (dsl/lf "P:shoe")
                true)
        expected (tr/build-atom-cat
                  {:type "nnp"
                   :fs (tr/build-fs
                        {:id "2"
                         :feats (list
                                 (tr/build-feat
                                  {:attr "index"
                                   :values (tr/build-lf (tr/build-nomvar "P"))})
                                 (tr/build-feat
                                  {:attr "design"
                                   :values (tr/build-featvar "DESIGN:garment")}))})
                   :lf (tr/build-lf
                        (tr/build-satop "P:shoe" (tr/build-prop "[*DEFAULT*]")))})]
    (compare-elements expected result)))

(deftest translate-lex-entry
  (testing "simple entry"
    (let [result (tr/lex-entry->entry (dsl/entry
                                    "Primary"
                                    (dsl/lf "P:shoe")
                                    (dsl/atomcat "nnp" {:index 2}
                                                 (dsl/fs-nomvar "index" "P")
                                                 (dsl/fs-featvar "design" "DESIGN:garment"))))
          expected (tr/build-entry
                                {:name "Primary"
                                 :category (tr/build-atom-cat
                                            {:type "nnp"
                                             :fs (tr/build-fs
                                                  {:id "2"
                                                   :feats (list
                                                           (tr/build-feat
                                                            {:attr "index"
                                                             :values (tr/build-lf
                                                                      (tr/build-nomvar "P"))})
                                                           (tr/build-feat
                                                            {:attr "design"
                                                             :values (tr/build-featvar "DESIGN:garment")}))})
                                             :lf (tr/build-lf
                                                  (tr/build-satop "P:shoe" (tr/build-prop "[*DEFAULT*]")))})})]
      (compare-elements expected result))
    (testing "complex entry"
      (let [result (tr/lex-entry->entry (dsl/entry
                                      "adj.full"
                                      (dsl/lf "U" nil
                                              (dsl/diamond "concrete" {:prop "true"})
                                              (dsl/diamond "gum-domain" {:nomvar "T"})
                                              (dsl/diamond "gum-attribute"
                                                           {:prop "[*DEFAULT*]"
                                                            :nomvar "P:gum-Color"
                                                            :diamond (dsl/diamond "concrete"
                                                                                  {:prop "true"})}))
                                      (dsl/<B
                                       (dsl/atomcat "np" nil
                                                    (dsl/fs-nomvar "index" "T"))
                                       (dsl/atomcat "np" 7
                                                    (dsl/fs-nomvar "index" "T")))))
            expected (tr/build-entry
                               {:type "adj.full"
                                :category (tr/build-complex-cat
                                           (tr/build-atom-cat
                                            {:type "np"
                                             :fs (tr/build-fs
                                                  {:inherits-from "1"
                                                   :feats (tr/build-feat
                                                           {:attr "index"
                                                            :values (tr/build-lf (tr/build-nomvar "T"))})})})
                                           (tr/build-slash {:mode "*" :dir "/"})
                                           (tr/build-atom-cat
                                            {:type "np"
                                             :fs (tr/build-fs
                                                  {:id "7"
                                                   :feats (tr/build-feat
                                                           {:attr "index"
                                                            :values (tr/build-lf (tr/build-nomvar "T"))})})})
                                           (tr/build-lf
                                            (tr/build-satop
                                             "U"
                                             (tr/build-diamond "concrete" (tr/build-prop "true"))
                                             (tr/build-diamond "gum-domain" (tr/build-nomvar "T"))
                                             (tr/build-diamond
                                              "gum-attribute"
                                              (tr/build-nomvar "P:gum-Color")
                                              (tr/build-prop "[*DEFAULT*]")
                                              (tr/build-diamond "concrete" (tr/build-prop "true"))))))})]))))

(deftest translate-complex-cat
  (let [lf (dsl/lf "E" "[*DEFAULT*]"
                   (dsl/diamond "Thing" {:nomvar "X"})
                   (dsl/diamond "Benefit" {:nomvar "Y"}))
        result (tr/category->entry (dsl/>F
                                 \>
                                 (dsl/<B
                                  (dsl/atomcat "s" nil (dsl/fs-nomvar "index" "E"))
                                  (dsl/atomcat "nnp" nil (dsl/fs-nomvar "index" "X")))
                                 (dsl/atomcat "np" nil (dsl/fs-nomvar "index" "Y")))
                                lf
                                true)
        expected (tr/build-complex-cat
                  (tr/build-atom-cat
                   {:type "s"
                    :fs (tr/build-fs
                         {:feats (list
                                  (tr/build-feat
                                   {:attr "index"
                                    :values (tr/build-lf (tr/build-nomvar "E"))}))})})
                  (tr/build-slash {:mode "*" :dir "\\"})
                  (tr/build-atom-cat
                   {:type "nnp"
                    :fs (tr/build-fs
                         {:feats (list
                                  (tr/build-feat
                                   {:attr "index"
                                    :values (tr/build-lf
                                             (tr/build-nomvar "X"))}))})})
                  (tr/build-slash {:mode ">" :dir "/"})
                  (tr/build-atom-cat
                   {:type "np"
                    :fs (tr/build-fs
                         {:feats (list
                                  (tr/build-feat
                                   {:attr "index"
                                    :values (tr/build-lf
                                             (tr/build-nomvar "Y"))}))})})
                  (tr/build-lf
                   (tr/build-satop
                    "E"
                    (tr/build-prop "[*DEFAULT*]")
                    (tr/build-diamond "Thing" (tr/build-nomvar "X"))
                    (tr/build-diamond "Benefit" (tr/build-nomvar "Y")))))]
    (compare-elements expected result)))
