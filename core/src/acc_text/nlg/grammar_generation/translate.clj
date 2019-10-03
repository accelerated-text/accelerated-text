(ns acc-text.nlg.grammar-generation.translate
  (:require [acc-text.nlg.spec.lexicon :as lex-spec]
            [acc-text.nlg.spec.morphology :as morph-spec]
            [acc-text.nlg.spec.feature-set :as fs-spec]
            [clojure.tools.logging :as log]
            [clojure.string :as str])
  (:import [org.jdom Element]
           [opennlp.ccg.lexicon Family EntriesItem DataItem]))

(declare print-element)

(defn normalize-pos [pos] (name pos))

(defn build-member [{:keys [stem pred]}]
  (if (nil? pred)
    (new DataItem stem stem)
    (new DataItem stem pred)))

(defn element->EntriesItem [family el]
  (try
    (new EntriesItem el family)
    (catch Exception ex
      (log/errorf "Failed to parse: %s. Reason: %s. \n StackTrace: %s" (print-element el) ex (str/join "\n" (.getStackTrace ex))))))

(defn build-custom-el [{name_ :name attrs :attrs children :children}]
  (let [el (new Element name_)]
    (doseq [[k v] attrs] (when-not (nil? v) (.setAttribute el (name k) v)))
    (doseq [c children] (when-not (nil? c) (.addContent el c)))
    el))

(defn build-prop [name]
  (build-custom-el {:name "prop" :attrs {:name name} :children []}))

(defn build-nomvar [name]
  (build-custom-el {:name "nomvar"
                    :attrs {:name name}
                    :children []}))

(defn build-diamond [mode & children]
  (build-custom-el {:name "diamond"
                    :attrs {:mode mode}
                    :children children}))

(defn build-satop [nomvar & children]
  (build-custom-el {:name "satop"
                    :attrs {:nomvar nomvar}
                    :children children}))

(defn build-lf [& children]
  (doto (new Element "lf")
    (.addContent children)))

(defn build-featvar [name]
  (doto (new Element "featvar")
    (.setAttribute "name" name)))

(defn- set-attr [element name value]
  (when value (.setAttribute element name value))
  element)

(defn- add-content [element values]
  (when values (.addContent element values))
  element)

(defn build-feat [{:keys [attr val values]}]
  (-> (new Element "feat")
      (set-attr "attr" attr)
      (set-attr "val" val)
      (add-content values)))

(defn build-fs
  [{:keys [id val attr inherits-from feats]}]
  (-> (new Element "fs")
      (set-attr "id" id)
      (set-attr "attr" attr)
      (set-attr "val" val)
      (set-attr "inheritsFrom" inherits-from)
      (add-content feats)))

(defn build-entry
  [{:keys [name stem index-rel active category]}]
  (log/tracef "Got entry to build, name: %s stem: %s index-rel: %s active: %s category: %s"
              name stem index-rel active category)
  (-> (new Element "entry")
      (set-attr "name" name)
      (set-attr "stem" stem)
      (set-attr "indexRel" index-rel)
      (set-attr "active" active)
      (add-content category)))

(defn build-atom-cat
  [{:keys [type fs lf]}]
  (log/tracef "AtomCat Got type: %s fs: %s lf: %s" type fs lf)
  (when (nil? type) (throw (AssertionError. "field `type` is required for Atomcat")))
  (-> (new Element "atomcat")
      (set-attr "type" type)
      (add-content fs)
      (add-content lf)))

(defn build-complex-cat [& cats]
  (let [cat (new Element "complexcat")]
    (log/tracef "Complex Cat from: %s" (pr-str cats))
    (add-content cat cats)))

(defn build-family
  [{:keys [name pos closed indexRel coartRel entries members] :or {closed false}}]
  (when (nil? name)
    (throw (AssertionError. "Family must have `name`")))
  (let [family (new Family name)
        built-entries (remove nil? (map (fn [el] (element->EntriesItem family el)) entries))]
    (log/tracef "Building family: %s, pos: %s, closed: %s Entries: %s Members: %s" name pos closed (pr-str entries) (pr-str members))
    (.setPOS family pos)
    (.setClosed family closed)
    (when-not (nil? indexRel)
      (.setIndexRel family indexRel))
    (when-not (nil? coartRel)
      (.setCoartRel family coartRel))
    (.setData family (into-array DataItem members))
    (.setEntries family (into-array EntriesItem built-entries))
    family))

(defn build-morph-entry
  [{:keys [pos word stem class macros]}]
  (-> (new Element "entry")
      (set-attr "pos" (name pos))
      (set-attr "word" word)
      (set-attr "stem" stem)
      (set-attr "class" class)
      (set-attr "macros" macros)))

(defn build-macro [name fs]
  (-> (new Element "macro")
      (set-attr "name" name)
      (add-content fs)))

(defn print-element
  [el]
  (let [el-type (.getName el)]
    (format
     "(%s %s %s)"
     el-type
     (case el-type
       "entry"   (.getAttribute el "name")
       "atomcat" (.getAttribute el "type")
       "fs"      (.getAttribute el "id")
       "feat"    (.getAttribute el "attr")
       "featvar" (.getAttribute el "name")
       "nomvar"  (.getAttribute el "name")
       "prop"    (.getAttribute el "name")
       "satop"   (.getAttribute el "nomvar")
       "")
     (pr-str (map print-element (.getChildren el))))))

(defn cons-fn [f item seq]
  (if-not (nil? item)
    (cons (f item) seq)
    seq))

(defn diamond->entry
  [{:keys [::lex-spec/mode
           ::lex-spec/nomvar
           ::lex-spec/diamonds
           ::lex-spec/prop]}]
  (let [children (->> (map diamond->entry diamonds)
                      (remove nil?) ;; If diamond doesn't have any children - we don't build it and we get null. Remove those
                      (cons-fn build-prop prop)
                      (cons-fn build-nomvar nomvar)
                      (seq))]
    (when (seq? children)
      (apply (partial build-diamond mode) children))))

(defn logical-form->entry [{:keys [::lex-spec/nomvar
                                   ::lex-spec/predicate
                                   ::lex-spec/diamonds]}]
  (build-lf
   (build-satop nomvar
                (->> (map diamond->entry diamonds)
                     (remove nil?) ;; If diamond doesn't have any children - we don't build it and we get null. Remove those
                     (cons-fn build-prop predicate)))))

(defn feature->entry
  [{:keys [::fs-spec/attribute
           ::fs-spec/feature-type
           ::fs-spec/value]}]
  (build-feat
   (if (= feature-type :feat)
     {:attr attribute :val value}
     {:attr attribute
      :values (case feature-type
                :featvar (build-featvar value)
                :nomvar (build-lf (build-nomvar value)))})))

(defn fs->entry
  [{:keys [::fs-spec/index
           ::fs-spec/inherits-from
           ::fs-spec/val
           ::fs-spec/attr
           ::fs-spec/features]}]
  (log/tracef "Index: %s Inherits: %s Features: %s" index inherits-from features)
  (build-fs {:id            (when index (str index))
             :val           val
             :attr          attr
             :inherits-from (when inherits-from (str inherits-from))
             :feats         (map feature->entry features)}))

(defn atomcat->entry [{:keys [::lex-spec/syntactic-type
                              ::lex-spec/feature-set]}
                      lf]
  (log/tracef "Parsing atomcat: Type: %s  FS: %s" syntactic-type feature-set)
  (when (nil? syntactic-type)
    (throw (AssertionError. "Syntactic type cannot be null")))
  (build-atom-cat
   {:type (str/lower-case (normalize-pos syntactic-type))
    :fs (fs->entry feature-set)
    :lf (when lf (logical-form->entry lf))}))

(defn build-slash
  [{:keys [mode dir]}]
  (let [slash (new Element "slash")]
    (if (nil? mode)
      (throw (AssertionError. "Field `mode` is required"))
      (.setAttribute slash "mode" mode))
    (if (nil? dir)
      (throw (AssertionError. "Field `mode` is required"))
      (.setAttribute slash "dir" dir))
    slash))

(defn slash->entry [slash dot]
  (build-slash {:dir (str slash) :mode (str dot)}))

(declare category->entry)

(defn complexcat->entry [[cat1 [slash dot] cat2]]
  (flatten
   [(category->entry cat1 nil false)
    (slash->entry slash dot)
    (category->entry cat2 nil false)]))

(defn category->entry
  [category lf root]
  (log/tracef "Got category: %s" category)
  (cond
    (contains? category :atomic-cat) (atomcat->entry (:atomic-cat category) lf)
    (contains? category :complex-cat) (if root
                                        (apply build-complex-cat
                                               (conj (vec (complexcat->entry (vec (:complex-cat category))))
                                                     (logical-form->entry lf)))
                                        (complexcat->entry (vec (:complex-cat category))))))

(defn lex-entry->entry [{:keys [::lex-spec/name
                                ::lex-spec/category
                                ::lex-spec/predicate
                                ::lex-spec/logical-form]}]
  (log/debugf "Name: %s Category: %s LF: %s" name category logical-form)
  (build-entry
   {:name     name
    :stem     predicate
    :category (category->entry category logical-form true)}))

(defn member->entry [{:keys [::lex-spec/stem
                             ::lex-spec/predicate]}]
  (build-member {:stem stem :pred predicate}))

(defn morph->entry [{:keys [::morph-spec/word
                            ::morph-spec/pos
                            ::morph-spec/predicate
                            ::morph-spec/class
                            ::morph-spec/macros]}]
  (log/debugf "Adding Morh word: %s stem: %s pos: %s class: %s macros: %s" word predicate pos class macros)
  (build-morph-entry
   {:word   word
    :pos    pos
    :stem   predicate
    :class  class
    :macros macros}))

(defn macro->entry [{:keys [::morph-spec/name
                            ::morph-spec/fs]}]
  (build-macro name (fs->entry fs)))

(defn family->entry [{:keys [::lex-spec/pos
                             ::lex-spec/name
                             ::lex-spec/closed
                             ::lex-spec/lexical-entries
                             ::lex-spec/members]}]
  (when (nil? pos)
    (throw (AssertionError. (format "Error in family: %s POS cannot be null." name))))
  (log/tracef "Family name: %s pos: %s closed?: %b" name (normalize-pos pos) closed)
  (build-family
   {:name    name
    :pos     (normalize-pos pos)
    :closed  closed
    :entries (map lex-entry->entry lexical-entries)
    :members (map member->entry members)}))
