(ns acc-text.nlg.gf.grammar
  (:require [acc-text.nlg.gf.grammar.function :as function]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::module keyword?)

(s/def ::instance keyword?)

(s/def ::flags (s/map-of #{:startcat} string? :min-count 1))

(s/def ::syntax (s/coll-of ::function/function))

(s/def ::grammar (s/keys :req [::module ::instance ::flags ::syntax]))

(defn build [module instance {::sg/keys [concepts relations]} context]
  {::module   module
   ::instance instance
   ::flags    {:startcat (function/concept->name (first concepts))}
   ::syntax   (let [concept-map (zipmap (map ::sg/id concepts) concepts)
                    relation-map (group-by ::sg/from relations)]
                (map (fn [{id ::sg/id :as concept}]
                       (let [relations (get relation-map id)
                             children (map #(get concept-map (::sg/to %)) relations)]
                         (function/build concept children relations context)))
                     concepts))})

(s/fdef build
        :args (s/cat :module ::module
                     :instance ::instance
                     :semantic-graph ::sg/graph
                     :context map?)
        :ret ::grammar)

(defn ->abstract [{::keys [module flags syntax]}]
  (format "abstract %s = {\n\tflags\n\t\t%s;\n\tcat\n\t\t%s;\n\tfun\n\t\t%s;\n}"
          (name module)
          (str/join ";\n\t\t" (map (fn [[flag val]]
                                     (format "%s = %s" (name flag) val))
                                   flags))
          (str/join ";\n\t\t" (cons (:startcat flags) (mapcat ::function/args syntax)))
          (->> syntax
               (map-indexed (fn [i {::function/keys [name args]}]
                              (format "Function%02d : %s"
                                      (inc i)
                                      (str/join " -> " (-> args (vec) (conj name))))))
               (str/join ";\n\t\t"))))

(defn ->concrete [{::keys [instance module syntax]}]
  (format "concrete %s of %s = {\n\tlincat\n\t\t%s;\n\tlin\n\t\t%s;\n}"
          (name instance)
          (name module)
          (->> syntax
               (group-by ::function/ret)
               (map (fn [[ret functions]]
                      (format "%s = {%s: %s}"
                              (str/join ", " (map ::function/name functions))
                              (name (nth ret 0))
                              (nth ret 1))))
               (str/join ";\n\t\t"))
          (->> syntax
               (map-indexed (fn [i {::function/keys [args ret body]}]
                              (format "Function%02d %s= {%s = %s}"
                                      (inc i)
                                      (str/join (interleave args (repeat " ")))
                                      (name (nth ret 0))
                                      (->> body
                                           (map (fn [{:keys [type value]}]
                                                  (case type
                                                    :literal (format "\"%s\"" (str/replace value #"\"" "\\\\\""))
                                                    :operator value
                                                    :function (format "%s.s" value))))
                                           (str/join " ")))))
               (str/join ";\n\t\t"))))
