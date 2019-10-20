(ns api.nlg.generator.parser-ng
  (:require [api.nlg.dictionary :as dictionary-api]
            [api.nlg.generator.amr :as amr-utils]
            [api.nlg.generator.ops :as ops]
            [api.nlg.generator.realizer :as realizer]
            [clojure.string :as str]
            [clojure.zip :as zip]
            [data.entities.amr :as amr-entity]
            [clojure.tools.logging :as log]))

(def synset
  {"provides"    ["provides"]
   "consequence" ["results"]})

(defn update-counter [counter result]
  (->> (:dynamic result)
       (filter #(get-in % [:name :dyn-name]))
       (count)
       (+ counter)))

(declare parse parse-node)

(defn parse-dynamic-item [type value attributes counter]
  {:dynamic [{:name  {type      value
                      :dyn-name (format "$%d" counter)}
              :attrs (cond-> (assoc attributes :source type)
                             (nil? (:type attributes)) (assoc :type type))}]})

(defn parse-dictionary-item [node attributes context]
  {:dynamic (reduce
              (fn [acc word]
                (conj acc {:name  word
                           :attrs (assoc attributes :type :wordlist :class (:name node))}))
              []
              (dictionary-api/search (str/lower-case (:name node)) (:reader-profile context)))})

(defn condition->gate [{:keys [operator type value1 value2]} attributes context counter]
  (let [cond-fn (case (keyword operator)
                  :== (partial =)
                  := (partial =))]
    (case (keyword type)
      :Value-comparison (fn [data]
                          (apply
                            cond-fn
                            (for [value [value1 value2]]
                              (-> (parse-node value attributes context counter)
                                  (get :dynamic)
                                  (first)
                                  (realizer/get-value data))))))))

(defn parse-conditional
  [node attributes context counter]
  (let [if-conditions (filter #(= "If-condition" (:type %)) (:conditions node))
        else-condition (first (filter #(= "Default-condition" (:type %)) (:conditions node)))
        else-gate #(not-any? (fn [gate] (gate %)) (for [{condition :condition} if-conditions]
                                                    (condition->gate condition attributes context counter)))]
    (apply
      merge-with
      concat
      (conj
        (map (fn [node]
               (parse-node
                 (:thenExpression node)
                 (assoc attributes :gate (condition->gate (:condition node) attributes context counter))
                 context
                 counter))
             if-conditions)
        (when (some? else-condition)
          (parse-node (:thenExpression else-condition) (assoc attributes :gate else-gate) context counter))))))

(defn parse-amr [node attributes context counter]
  (let [verbclass (amr-entity/get-verbclass (:conceptId node))
        members (for [item (dictionary-api/search (get-in node [:dictionaryItem :itemId]) (:reader-profile context))]
                  {:name item})                             ;; Our dictionary-items are becoming grammar's members
        roles (reduce
                (fn [fragment [counter {name :name :as role}]]
                  (merge-with
                    concat
                    fragment
                    (parse role (assoc attributes :amr true :title name) context (update-counter counter fragment))))
                {:dynamic []}
                (map vector (repeat (inc counter)) (:roles node)))
        results (amr-utils/generate-results
                  (amr-utils/build-grammars verbclass members)
                  (conj
                    (map amr-utils/to-placeholder (map :type (:thematic-roles verbclass)))
                    ;; First verb from our dictionary
                    (:name (first members))))
        restrictors (amr-utils/build-restrictors verbclass (amr-utils/amr-keys->data-keys roles))
        replacements (log/spy (reduce (fn [m {{title :title} :attrs {dyn-name :dyn-name} :name}]
                                        (assoc m (amr-utils/to-placeholder title) dyn-name))
                                      {}
                                      (:dynamic roles)))]
    (when (seq results)
      (merge-with
        concat
        {:dynamic [{:name  {:quotes   (flatten
                                        (map (fn [rules restrictor] ;; All of the AMR variations are saved as array of quotes
                                               (for [rule rules]
                                                 {:gate  restrictor
                                                  :value (ops/replace-multi rule replacements)}))
                                             results restrictors))
                            :dyn-name (format "$%d" counter)}
                    :attrs (assoc attributes :source :quotes :type :amr)}]}
        roles))))

(defn parse-list [type node attributes context counter]
  (case type
    :Any-of (parse (rand-nth (:children node)) attributes context counter)))

(defn parse-relationship [{children :children :as node} attributes context counter]
  (merge-with
    concat
    {:static (some-> (get synset (:relationshipType node)) (rand-nth) (vector))}
    (parse {:children children} (assoc attributes :type :benefit) context counter)))

(defn parse-node [{:keys [name text type] :as node} attributes context counter]
  (case (keyword type)
    :Cell (parse-dynamic-item :cell (keyword name) attributes counter)
    :Dictionary-item-modifier (parse-dynamic-item :modifier name attributes counter)
    :Quote (parse-dynamic-item :quote text attributes counter)
    :Dictionary-item (parse-dictionary-item node attributes context)
    :Product-component (parse-node name (assoc attributes :type :component) context counter)
    :Product (parse-node name (assoc attributes :type :product) context counter)
    :Relationship (parse-relationship node attributes context counter)
    :One-of-synonyms (parse-list :Any-of node attributes context counter)
    :AMR (parse-amr node attributes context counter)
    :If-then-else (parse-conditional node attributes context counter)
    nil))

(defn parse [root attributes context counter]
  (loop [zipper (zip/zipper
                  map?
                  (fn [{type :type :as branch}]
                    (when-not (contains? #{"AMR" "Relationship" "One-of-synonyms"} type)
                      (cond
                        (:segments branch) (:segments branch)
                        (:roles branch) (:roles branch)
                        (:children branch) (:children branch)
                        (:child branch) (-> branch :child vector))))
                  (fn [root children]
                    (assoc root :children children))
                  root)
         result {:static [] :dynamic []}
         counter counter]
    (if (zip/end? zipper)
      (-> result
          (update :static vec)
          (update :dynamic vec))
      (let [fragment (parse-node (zip/node zipper) attributes context counter)]
        (recur
          (zip/next zipper)
          (log/spyf "Result: %s" (merge-with concat result fragment))
          (update-counter counter fragment))))))

(defn parse-document-plan [document-plan attributes context]
  (-> document-plan
      (parse attributes context 1)
      (merge context)))
