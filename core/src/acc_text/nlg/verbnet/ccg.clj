(ns acc-text.nlg.verbnet.ccg
  (:require [acc-text.nlg.ccg.base-en :as base-en]
            [acc-text.nlg.dsl.core
             :refer
             [<B
              >F
              atomcat
              entry
              family
              fs-featvar
              fs-nomvar
              lf
              member
              morph-entry]]
            [acc-text.nlg.grammar :as grammar]
            [acc-text.nlg.grammar-generation.translate :as translate]
            [acc-text.nlg.verbnet.grammar-patterns :as gp]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [acc-text.nlg.gf.cf-format :as cf]))

(def loner->pred
  {:N  :NP
   :LX :LEX
   :P  :PREP
   :VB :VERB
   :A  :ADV
   :IN :IN})

(def pred->loner
  ;;invert `loner-pred` map
  (reduce-kv (fn [m k v] (assoc m v k)) {} loner->pred))

(defn is-implicit? [{pos :pos}] (contains? #{:PREP :POST :LEX :Conj :IN} pos))

(defn extract-implicit [syntax]
  (->> syntax
       (filter is-implicit?)
       (map (fn [{:keys [value pos]}] [value (get pred->loner pos pos)]))))

(defn vclass->morph [{:keys [id members thematic-roles adverbs]}]
  (concat
    (map
      (fn [{:keys [name macros]}] (morph-entry name :VB {:stem (gp/drop-vnet-id id) :macros macros}))
      members)
    ;;FIXME when do we get adverbs from vclass?
    (map
      (fn [{name :name}] (morph-entry name :A {:stem (format "%s-adv" (gp/drop-vnet-id id))}))
      adverbs)
    (map (fn [{type :type}]
           (morph-entry (format "{{%s}}" (string/upper-case type)) :N {:stem type}))
         thematic-roles)))

(def <<-compose [<B <B])
(def ><-compose [>F <B])
(def <>-compose [<B >F])
(def >>-compose [>F >F])

(def placement-slash {:start  ><-compose
                      :end    <<-compose
                      :middle <>-compose})

(defn placement->slashes
  ([placement] (get placement-slash placement))
  ([placement parent]
   (if (and (= :middle placement) (= :middle parent)) >>-compose (placement->slashes placement))))

(defn nomvar-name [order-fn value] (format "X%d:%s" (order-fn) (string/lower-case value)))

(defn lf-name [symbol-name value] (format "%s:%s" symbol-name (string/lower-case value)))

(defn arg->ccg-cat
  "Builds complex categories.
   If recursive is true - we go through whole tree and build rules
   If false - we just add root node as a category, expect other rules to build upon it"
  [order-fn {:keys [predicate arg1 arg2 pos value placement]} parent-placement]
  (if predicate
    (let [[outer-slash inner-slash] (placement->slashes placement parent-placement)]
      (if arg2
        (outer-slash
          (arg->ccg-cat order-fn arg2 placement)
          (inner-slash
            (atomcat (:pos predicate) {}
                     (fs-nomvar "index" (nomvar-name order-fn (:value predicate))))
            (arg->ccg-cat order-fn arg1 placement)))

        (outer-slash
          (arg->ccg-cat order-fn arg1 placement)
          (atomcat (:pos predicate) {}
                   (fs-nomvar "index" (nomvar-name order-fn (:value predicate)))))))

    (atomcat pos {} (fs-nomvar "index" (nomvar-name order-fn value)))))

(defn build-atomcat [pos index symbol role]
  (atomcat pos {:index index}
           (fs-nomvar "index" symbol)
           (fs-featvar "role" (clojure.string/lower-case role))))

(def static-restrictors
  {:that_comp         "that"
   :what_extract      "what"
   :oc_to_inf         "to"
   :np_to_inf         "to"
   :np_on_inf         "on"
   :wh_inf            "how to"
   :time_future_fixed "will"})

(defn static-restrictor?
  [{type :type}]
  (contains? static-restrictors (keyword type)))

(defn build-restrictors
  "At the moment, only single level of restrictors supported"
  [[{type :type} & _] parent index-fn symbol-fn]
  (let [value (format "%s_restrictor" parent)
        S     (symbol-fn)
        stem  (get static-restrictors (keyword type))]
    (family value :IN false
            (entry "Restrictor" (lf (lf-name S value))
                   (build-atomcat :RSTR (index-fn) S value))
            (member stem))))

(defn- restrictor-atomcat [value]
  (atomcat :RSTR {}
           (fs-nomvar "index"
                      (nomvar-name (fn [] 1) (format "%s_restrictor" value)))))

(defn- <B-family [family-name index symbol value pos]
  (family value (get pred->loner pos) true
          (entry family-name (lf (lf-name symbol value))
                 (<B (build-atomcat pos index symbol value)
                     (restrictor-atomcat value)))
          (member value)))

(defn- simple-family [family-name index symbol value pos]
  (family value (get pred->loner pos) true
          (entry family-name (lf (lf-name symbol value))
                 (build-atomcat pos index symbol value))
          (member value)))

(defn get-parts
  "Every word must have rule for it, otherwise it will resolve to nothing.
  This is just a quick hack to create those rules"
  [index-fn symbol-fn & args]
  (->> args
       (map
         (fn [{:keys [arg1 arg2 pos value restrictors] :as part {pred-restrictors :restrictors pred-pos :pos pred-val :value :as predicate} :predicate}]
           (log/debugf "Part: %s" part)
           (let [S (symbol-fn)]
             (if predicate
               (if (and pred-restrictors (some static-restrictor? pred-restrictors))
                 (cons [(build-restrictors pred-restrictors pred-val index-fn symbol-fn)
                        (<B-family "Predicate" (index-fn) S pred-val pred-pos)]
                       (get-parts index-fn symbol-fn arg1 arg2))
                 (cons (simple-family "Predicate" (index-fn) S pred-val pred-pos)
                       (get-parts index-fn symbol-fn arg1 arg2)))

               (when (contains? #{:NP :LEX :PREP :ADV} pos)
                 (if (and restrictors (some static-restrictor? restrictors))
                   [(build-restrictors restrictors value index-fn symbol-fn)
                    (<B-family "Primary" (index-fn) S value pos)]
                   (simple-family "Primary" (index-fn) S value pos)))))))
       (flatten)
       (remove nil?)))

(defn apply-restrictors [value restrictors root-cat]
  (log/debugf "Restrictors: %s" restrictors)
  (if-not (empty? restrictors)
    (<B
     root-cat
     (atomcat :RSTR {} (fs-nomvar "index" (nomvar-name (fn [] 1) (format "%s_restrictor" value)))))
    root-cat))

(defn build-ccg
  [idx-fn symbol-fn {:keys [predicate arg1 arg2 placement] :as root}]
  (log/debugf "Root category: %s" root)
  (let [{:keys [value pos restrictors]} predicate
        [outer-slash inner-slash]       (placement->slashes placement)
        order                           (atom 0)
        order-fn                        (fn [] (swap! order inc))
        parts                           (concat
                                          (get-parts idx-fn symbol-fn arg1 arg2)
                                          (when (seq restrictors)
                                            (list (build-restrictors restrictors value idx-fn symbol-fn))))
        main-family                     (family "AMR" (get pred->loner pos) true
                                                (entry "Primary"
                                                       (lf (symbol-fn))
                                                       (apply-restrictors
                                                         value
                                                         restrictors
                                                         (if arg2
                                                           (outer-slash
                                                             (atomcat :S
                                                                      {:index (idx-fn)}
                                                                      (fs-nomvar "index" (nomvar-name order-fn value)))
                                                             (inner-slash
                                                               (arg->ccg-cat order-fn arg1 placement)
                                                               (arg->ccg-cat order-fn arg2 placement)))

                                                           (inner-slash
                                                             (atomcat :S
                                                                      {:index (idx-fn)}
                                                                      (fs-nomvar "index" (nomvar-name order-fn value)))
                                                             (arg->ccg-cat order-fn arg1 placement)))))
                                                (member value))]
    (log/debugf "Parts: %s" (pr-str parts))
    (log/debugf "Main-Family: %s" main-family)
    (cons
      main-family
      parts)))

(defn frame->complex-categories [id {syntax :syntax}]
  (let [idx         (atom 20)
        symbols     (->> (range (int \A) (inc (int \Z)))
                         (map (comp str char))
                         (vec)
                         (atom))
        next-idx    (fn [] (swap! idx inc))
        next-symbol (fn []
                      (let [result (peek @symbols)]
                        (swap! symbols pop)
                        result))]
    (->> (gp/build-grammar-patterns id syntax)
         (map (partial build-ccg next-idx next-symbol))
         (flatten))))

(defn vn->grammar
  [vn]
  (let [morph         (vclass->morph vn)
        base-morph    (map (fn [[_ v]] (morph-entry v :IN {:stem v})) static-restrictors) 
        morph-entries (map translate/morph->entry (concat morph base-morph))
        base-families base-en/initial-families
        macros        []]
    (map
      (fn [frame]
        (let [grammar-builder (grammar/build-grammar {:types (grammar/build-types (list
                                                                                    {:name "sem-obj"}
                                                                                    {:name "phys-obj" :parents "sem-obj"}))
                                                      :rules (grammar/build-default-rules)})
              lex             (frame->complex-categories (:id vn) frame)
              implicit        (map (fn [[word pos]] (translate/morph->entry
                                                      (morph-entry word pos {:stem word})))
                                   (->> frame
                                        (:syntax)
                                        (extract-implicit)))
              lexicon         (grammar/build-lexicon
                                {:families (map translate/family->entry (concat base-families lex))
                                 :morph    (concat morph-entries implicit)
                                 :macros   (map translate/macro->entry macros)})]
          (grammar-builder lexicon)))
      (:frames vn))))

(defn frame->cf
  [themrole-idx initial {syntax :syntax}]
  (let [[head & body] (map
                       (fn
                         [{:keys [pos value]}]
                         (case pos
                           :NP (get themrole-idx value)
                           :LEX (format "\"%s\"" value)
                           :VERB "VB"
                           :PREP (format "\"%s\"" value)))
                       syntax)]
    (concat
     [(cf/gf-syntax-item "Pred" "S" (format "%s VP" head))
     (cf/gf-syntax-item "Compl" "VP" (string/join " " body))]
     initial)))

(defn vn->cf
  [{:keys [members frames thematic-roles]}]
  (let [themrole-idx (into {} (map-indexed (fn [idx {type :type}] [type (format "NP%d" idx)]) thematic-roles))
        initial (concat
                 (map (fn [{name :name}] (cf/gf-morph-item "Action" "VB" name)) members)
                 (map (fn [[k v]] (cf/gf-morph-item "Actor" v (cf/data-morphology-value k))) themrole-idx))]
    (-> (partial frame->cf themrole-idx initial)
        (map frames))))
