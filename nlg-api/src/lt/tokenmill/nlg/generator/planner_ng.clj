(ns lt.tokenmill.nlg.generator.planner-ng
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [lt.tokenmill.nlg.generator.parser-ng :as parser]
            [ccg-kit.grammar :as ccg]
            [ccg-kit.grammar-generation.morphology :as ccg-morphology]
            [ccg-kit.grammar-generation.lexicon :as ccg-lexicon]
            [ccg-kit.grammar-generation.xml-utils :as ccg-xml]
            [ccg-kit.spec.ccg :as ccg-spec]
            [lt.tokenmill.nlg.db.s3 :as s3]
            [lt.tokenmill.nlg.db.config :as config]
            [lt.tokenmill.nlg.generator.ops :as ops]
            [lt.tokenmill.nlg.generator.realizer :as realizer]))

(defn build-dp-instance
  "dp - a hashmap compiled with `compile-dp`
   data - a flat hashmap (represents CSV)
   returns: hashmap (context) which will be used to generate text"
  [dp]
  (loop [context {:dynamic []
                  :static []
                  :reader-profile :default}
         fs dp]
    (if (empty? fs)
      context
      (let [[head & tail] fs]
        (log/tracef "Head: %s" head)
        (recur (ops/merge-context context (into {} head)) tail)))))

(defn download-grammar
  "Downloads latest version of Grammar and returns it's path"
  []
  (do
    (.mkdir (java.io.File. "/tmp/ccg"))
    (.mkdir (java.io.File. "/tmp/ccg/grammar/"))
    (let []
      (s3/download-dir config/grammar-bucket "grammar" "/tmp/ccg/")
      "/tmp/ccg/grammar")))


(defn resolve-item-context
  [item]
  (let [type-name (get-in item [:attrs :type])
        default {:pos :NP}]
    (case type-name
      :product {:class "product"
                :pos :NNP}
      :benefit {:class "benefit"
                :pos :NP}
      :component {:class "component"
                  :pos :NNP}
      default)))

(defn resolve-morph-context
  [group]
  (map (fn
         [item]
         (let [name (case (string? (item :name))
                      true (item :name)
                      false ((item :name) :dyn-name))
               context (resolve-item-context item)]
           #::ccg-spec{:syntactic-type (context :pos)
                       :pos (context :pos)
                       :word name
                       :class (context :class)}))
       group))

(defn zipWith [left right] (map vector left right))

(defn resolve-lex-context
  [idx items]
  (let [predicate "[*DEFAULT*]"
        context (resolve-item-context (first items))
        category #::ccg-spec{:syntactic-type :NP
                             :feature-set [idx []]}
        lf #::ccg-spec{:nomvar "X"}
        entries (list (let [item (first items)]
                       #::ccg-spec {:predicate predicate
                                    :category category
                                    :pos (context :pos)
                                    :logical-form lf}))]
    #::ccg-spec{:pos (context :pos)
                :name (context :pos)
                :lexical-entries entries}))

(defn compile-custom-grammar
  "TODO: some magic should happen here"
  [root-path values]
  (log/debug "\n--------------------\nCompiling Grammar\n--------------------")
  (log/debugf "Dynamic values: %s" (pr-str values))
  (let [grouped (group-by (fn [item] (get-in item [:attrs :type])) values)
        morphology-context (map resolve-morph-context (vals grouped))
        morphology (map ccg-morphology/generate-morphology-xml morphology-context)
        lexicon (map (fn [[l m]]
                       (ccg-lexicon/generate-lexicon-xml (list l) m))
                     (zipWith (map-indexed resolve-lex-context (vals grouped)) morphology-context))]
    (ccg-xml/write-xml (format "%s/gen-morph.xml" root-path) (flatten morphology))
    (ccg-xml/write-xml (format "%s/gen-lexicon.xml" root-path) (flatten lexicon))))


(defn get-placeholder
  [item]
  (if (realizer/placeholder? item)
    (get-in item [:name :dyn-name])
    (item :name)))

(defn generate-templates
  "Takes context and generates numerous sentences. Picks random one"
  [grammar-path context]
  (let [dyn-values (map get-placeholder (:dynamic context))
        values (concat (distinct (:static context)) dyn-values)
        _ (log/debugf "Context: %s" context)
        generated (apply (partial ccg/generate (ccg/load-grammar (format "%s/grammar.xml" grammar-path))) (ops/distinct-wordlist values))]
    {:context context
     :templates generated}))

(defn build-segment
  [grammar-path segment]
  (let [instances (map build-dp-instance segment)
        templates (map (partial generate-templates grammar-path) instances)]
    templates))

(defn render-segment
  [templates data]
  (let [realized (map (partial realizer/realize data) templates)
        _ (log/debugf "Realized: %s" (pr-str realized))
        sentences (map #(if (empty? %) "" (rand-nth %)) realized)]
    (string/join ". " sentences)))

(defn render-dp
  "document-plan - a hash map with document plan
   data - list of rows of hashmap (represents CSV)
   reader-profile - key defining reader (user)
   returns: generated text"
  [document-plan data reader-profile]
  (let [dp (parser/parse-document-plan document-plan {} {:reader-profile reader-profile})
        grammar-path (download-grammar)
        instances (map #(map build-dp-instance %) dp)
        context (ops/merge-contexts {:static [] :dynamic []} (flatten instances))
        _ (compile-custom-grammar grammar-path (:dynamic context))
        templates (map (partial build-segment grammar-path) dp)
        _ (log/debugf "Templates: %s" (pr-str templates))]

    (map (fn
           [row]
           (let [segments (map #(render-segment % row) templates)]
             (string/trim (string/join "" segments)))) data)))
