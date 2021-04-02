(ns api.nlg.parser
  (:require [acc-text.nlg.gf.operations :as ops]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.zip :as zip]
            [data.entities.document-plan :as dp-entity]
            [data.entities.document-plan.utils :as dp-utils]
            [data.entities.document-plan.zip :as dp-zip]
            [data.utils :as utils]))

(def constants #{"*Language" "*Definition" "*Reader"})

(defmulti build-semantic-graph (fn [{type :type concept-id :conceptId} {kind :kind}]
                                 (keyword
                                   (case [kind type]
                                     ["AMR" "Document-plan"] "AMR-plan"
                                     ["RGL" "Document-plan"] "AMR-plan"
                                     ["AMR" "Segment"] "Frame"
                                     ["RGL" "Segment"] "Frame"
                                     ["AMR" "AMR"] (if (contains? ops/operation-map concept-id) "Operation" "AMR")
                                     ["RGL" "AMR"] (if (contains? ops/operation-map concept-id) "Operation" "AMR")
                                     type))))

(defn definition? [node]
  (= "Define-var" (:type node)))

(defmethod build-semantic-graph :default [{:keys [id type]} _]
  (throw (Exception. (format "Unknown node type for node id %s: %s" id type))))

(defmethod build-semantic-graph :placeholder [{id :id position :position} _]
  #::sg{:concepts [{:id       id
                    :position position
                    :type     :placeholder}]})

(defmethod build-semantic-graph :Document-plan [{:keys [id segments position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :document-plan}]
        :relations (->> segments
                        (remove definition?)
                        (map-indexed (fn [index {segment-id :id}]
                                       {:from  id
                                        :to    segment-id
                                        :role  :segment
                                        :index index})))})

(defmethod build-semantic-graph :AMR-plan [{:keys [id segments kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :document-plan
                     :category kind}]
        :relations (->> segments
                        (remove definition?)
                        (map-indexed (fn [index {frame-id :id}]
                                       {:from     id
                                        :to       frame-id
                                        :role     :frame
                                        :index    index
                                        :category kind})))})

(defmethod build-semantic-graph :Segment [{:keys [id children position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :segment}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from  id
                                   :to    child-id
                                   :role  :instance
                                   :index index})
                                children)})

(defmethod build-semantic-graph :Frame [{:keys [id children kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :frame
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from     id
                                   :to       child-id
                                   :role     :instance
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :AMR [{id :id concept-id :conceptId roles :roles kind :kind position :position} _]
  #::sg{:concepts  [(let [{name :name} (dp-entity/get-document-plan concept-id)]
                      {:id       id
                       :position position
                       :type     :amr
                       :label    (str name)
                       :name     concept-id
                       :category kind})]
        :relations (map-indexed (fn [index {[{child-id :id}] :children kind :name label :label}]
                                  {:from     id
                                   :to       child-id
                                   :role     :arg
                                   :index    index
                                   :category kind
                                   :name     label})
                                roles)})

(defmethod build-semantic-graph :Operation [{id :id concept-id :conceptId roles :roles kind :kind position :position} _]
  #::sg{:concepts  [(let [{:keys [module name]} (get ops/operation-map concept-id)]
                      {:id       id
                       :position position
                       :type     :operation
                       :name     name
                       :label    concept-id
                       :category kind
                       :module   module})]
        :relations (map-indexed (fn [index {[{child-id :id}] :children kind :name label :label}]
                                  {:from     id
                                   :to       child-id
                                   :role     :arg
                                   :index    index
                                   :category kind
                                   :name     label})
                                roles)})

(defmethod build-semantic-graph :Cell [{:keys [id name position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :data
                     :name     name
                     :category "Str"}]
        :relations []})

(defmethod build-semantic-graph :Quote [{:keys [id text position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :quote
                     :value    (str text)
                     :category "Str"}]
        :relations []})

(defmethod build-semantic-graph :Dictionary-item [{:keys [id itemId name kind position]} _]
  #::sg {:concepts  [{:id       id
                      :position position
                      :type     :dictionary-item
                      :name     itemId
                      :label    name
                      :category kind}]
         :relations []})

(defmethod build-semantic-graph :Dictionary-item-modifier [{:keys [id itemId name kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :dictionary-item
                     :name     itemId
                     :label    name
                     :category kind}]
        :relations []})

(defmethod build-semantic-graph :Cell-modifier [{:keys [id name position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :data
                     :name     name
                     :category "Str"}]
        :relations []})

(defmethod build-semantic-graph :Modifier [{:keys [id child modifiers kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :modifier
                     :category kind}]
        :relations (cons {:from     id
                          :to       (:id child)
                          :role     :child
                          :category (:kind child)}
                         (map-indexed (fn [index {modifier-id :id kind :kind}]
                                        {:from     id
                                         :to       modifier-id
                                         :role     :modifier
                                         :index    index
                                         :category kind})
                                      modifiers))})

(defmethod build-semantic-graph :Sequence [{:keys [id children kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :sequence
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from     id
                                   :to       child-id
                                   :role     :item
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :Shuffle [{:keys [id children kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :shuffle
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from     id
                                   :to       child-id
                                   :role     :item
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :One-of-synonyms [{:keys [id children kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :synonyms
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from     id
                                   :to       child-id
                                   :role     :item
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :If-then-else [{:keys [id conditions kind position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :condition
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from     id
                                   :to       child-id
                                   :role     :statement
                                   :index    index
                                   :category kind})
                                conditions)})

(defmethod build-semantic-graph :If-condition [{id :id kind :kind condition :condition then-expression :thenExpression position :position} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :if-statement
                     :category kind}]
        :relations [{:from     id
                     :to       (:id condition)
                     :role     :predicate
                     :category kind}
                    {:from     id
                     :to       (:id then-expression)
                     :role     :then-expression
                     :category kind}]})

(defmethod build-semantic-graph :Default-condition [{id :id kind :kind then-expression :thenExpression position :position} _]
  (when (some? then-expression)
    #::sg{:concepts  [{:id       id
                       :position position
                       :type     :else-statement
                       :category kind}]
          :relations [{:from     id
                       :to       (:id then-expression)
                       :role     :then-expression
                       :category kind}]}))

(defmethod build-semantic-graph :Value-comparison [{:keys [id kind operator value1 value2 position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :comparator
                     :value    operator
                     :category kind}]
        :relations [{:from     id
                     :to       (:id value1)
                     :role     :comparable
                     :category kind
                     :index    0}
                    {:from     id
                     :to       (:id value2)
                     :role     :comparable
                     :category kind
                     :index    1}]})

(defmethod build-semantic-graph :Value-in [{:keys [id kind operator value list position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :comparator
                     :value    operator
                     :category kind}]
        :relations [{:from     id
                     :to       (:id list)
                     :role     :comparable
                     :category kind
                     :index    0}
                    {:from     id
                     :to       (:id value)
                     :role     :comparable
                     :category kind
                     :index    1}]})

(defmethod build-semantic-graph :And-or [{:keys [id kind operator children position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :boolean
                     :value    operator
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from     id
                                   :to       child-id
                                   :role     :input
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :Not [{id :id kind :kind {child-id :id} :value position :position} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :boolean
                     :value    "not"
                     :category kind}]
        :relations [{:from     id
                     :to       child-id
                     :role     :input
                     :category kind}]})

(defmethod build-semantic-graph :Xor [{:keys [id kind value1 value2 position]} _]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :boolean
                     :value    "xor"
                     :category kind}]
        :relations [{:from     id
                     :to       (:id value1)
                     :role     :input
                     :category kind}
                    {:from     id
                     :to       (:id value2)
                     :role     :input
                     :category kind}]})

(defmethod build-semantic-graph :Define-var [{:keys [id kind value name position]} {labels :labels}]
  #::sg{:concepts  [{:id       id
                     :position position
                     :type     :variable
                     :category kind
                     :name     (get labels name kind)
                     :label    name}]
        :relations [{:from     id
                     :to       (:id value)
                     :role     :definition
                     :category kind}]})

(defmethod build-semantic-graph :Get-var [{id :id var-id :name kind :kind position :position} {variables :variables labels :labels}]
  (let [label (get labels var-id)]
    #::sg{:concepts  [{:id       id
                       :position position
                       :type     (if (contains? constants label) :constant :reference)
                       :name     (or label kind)
                       :label    var-id
                       :category kind}]
          :relations (map-indexed (fn [index {var-id :id}]
                                    {:from     id
                                     :to       var-id
                                     :role     :pointer
                                     :index    index
                                     :category kind})
                                  (get variables var-id))}))

(declare preprocess-node)

(defn gen-id [{:keys [id srcId] :as node}]
  (-> node
      (assoc :id (or id srcId (utils/gen-rand-str 20)))
      (dissoc :srcId)))

(defn nil->placeholder [node]
  (cond-> node (nil? node) (assoc :type "placeholder")))

(defn rearrange-modifiers [node index]
  (loop [zipper (dp-zip/make-zipper node)
         modifiers []]
    (let [{:keys [type child] :as node} (zip/node zipper)]
      (if-not (and (contains? #{"Dictionary-item-modifier" "Cell-modifier"} type) (some? child))
        (if (seq modifiers)
          (-> {:srcId (str (:srcId node) "/mod") :type "Modifier"}
              (dp-zip/make-node (cons node modifiers))
              (preprocess-node index))
          node)
        (recur (zip/next zipper) (conj modifiers (dissoc node :child)))))))

(defn preprocess-node [node index]
  (-> node (nil->placeholder) (rearrange-modifiers index) (assoc :position index) (gen-id)))

(defn preprocess [root]
  (loop [loc (dp-zip/make-zipper root)
         index 1]
    (if (zip/end? loc)
      (zip/node loc)
      (-> loc
          (zip/edit preprocess-node index)
          (zip/next)
          (recur (inc index))))))

(defn select-kind [kinds]
  (cond
    (= 1 (count (set kinds))) (first kinds)))

(defn post-add-kind [{:keys [name type kind] :as node} {variables :variables}]
  (let [kinds (map :kind (remove definition? (dp-zip/get-children node)))
        kind (or
               kind
               (cond
                 (= "Get-var" type) (select-kind (remove nil? (map :kind (get variables name))))
                 (contains? #{"If-then-else" "Xor"} type) (select-kind (set (remove nil? kinds)))
                 :else (select-kind kinds)))]
    (when (some? node)
      (assoc node :kind kind))))

(defn post-process [root context]
  (loop [loc (-> root (dp-zip/make-zipper) (dp-zip/post-zip))]
    (if (zip/end? loc)
      (zip/node loc)
      (recur
        (-> loc
            (zip/edit post-add-kind context)
            (dp-zip/post-next))))))

(defn add-category [semantic-graph {:keys [id kind name] :as node} {variables :variables}]
  (let [in-relations (filter #(= (:to %) id) (::sg/relations semantic-graph))
        kind (or
               kind
               (case (keyword type)
                 :Get-var (or (select-kind (remove nil? (map :category in-relations)))
                              (some #(when (contains? % :category)
                                       (:category %))
                                    (get variables name)))
                 (select-kind (remove nil? (map :category in-relations)))))]
    (when (some? node)
      (cond-> node
              (some? kind) (assoc :kind kind)))))

(defn document-plan->semantic-graph
  ([{id :id uid :uid name :name kind :kind body :documentPlan blockly-xml :blocklyXml :as dp}]
   (let [labels (dp-utils/get-variable-labels blockly-xml)
         variables (dp-utils/find-variables dp labels)
         context {:labels labels :variables variables}]
     (loop [semantic-graph #::sg{:id          (or id (utils/gen-rand-str 16))
                                 :uid         uid
                                 :name        (str name)
                                 :kind        kind
                                 :category    "Str"
                                 :description (str/join "\n" (dp-utils/find-examples dp))
                                 :relations   []
                                 :concepts    []}
            loc (-> body (preprocess) (post-process context) (dp-zip/make-zipper))
            context (assoc context :variables {})]
       (if (or (zip/end? loc) (empty? body))
         (-> semantic-graph
             (sg-utils/prune-nil-relations)
             (sg-utils/prune-concepts-by-type :placeholder)
             (sg-utils/prune-unrelated-branches)
             (sg-utils/add-category)
             (sg-utils/remove-nil-categories)
             (sg-utils/sort-semantic-graph)
             (update ::sg/concepts set)
             (update ::sg/relations set))
         (let [{:keys [type name] :as node} (add-category semantic-graph (zip/node loc) (merge dp context))]
           (recur
             (sg-utils/merge-semantic-graphs
               semantic-graph
               (build-semantic-graph node (merge dp context)))
             (-> loc
                 (zip/replace node)
                 (zip/next))
             (-> context
                 (update :variables #(if (= "Define-var" type)
                                       (merge-with concat % {name [node]}) %))))))))))

(s/fdef document-plan->semantic-graph
        :args (s/cat :document-plan map?)
        :ret ::sg/graph)
