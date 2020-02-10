(ns data.datomic.blockly
  (:require
    [data.utils :as utils]
    [data.datomic.utils :refer [remove-nil-vals]]
    [datomic.api :as d]))

(defn remove-empty-or-nil-vals [m]
  (into {}
        (remove (fn [[_ v]] (or (nil? v)
                                (and (not (boolean? v)) (empty? v))
                                (= [nil] v))) m)))

(defn remove-empty-or-nil-but-not-nil-list-vals [m]
  (into {}
        (remove (fn [[_ v]] (or (nil? v)
                                (and (not (boolean? v))
                                     (not (vector? v))
                                     (empty? v)))) m)))

(defn prepare-document-plan [document-plan]
  (when document-plan
    (remove-empty-or-nil-vals
      {:blockly/segments        (map prepare-document-plan (:segments document-plan))
       :blockly/children        (map prepare-document-plan (:children document-plan))
       :blockly/conditions      (map prepare-document-plan (:conditions document-plan))
       :blockly/hasChildren     (not (nil? (:children document-plan)))
       :blockly/srcId           (:srcId document-plan)
       :blockly/type            (:type document-plan)
       :blockly/name            (:name document-plan)
       :blockly/label           (:label document-plan)
       :blockly/text            (:text document-plan)
       :blockly/concept-id      (:conceptId document-plan)
       :blockly/kind            (:kind document-plan)
       :blockly/item-id         (:itemId document-plan)
       :blockly/operator        (:operator document-plan)
       :blockly/roles           (map prepare-document-plan (:roles document-plan))
       :blockly/child           (prepare-document-plan (:child document-plan))
       :blockly/condition       (prepare-document-plan (:condition document-plan))
       :blockly/then-expression (prepare-document-plan (:thenExpression document-plan))
       :blockly/list            (prepare-document-plan (:list document-plan))
       :blockly/modifier        (prepare-document-plan (:modifier document-plan))
       :blockly/value           (prepare-document-plan (:value document-plan))
       :blockly/value-1         (prepare-document-plan (:value1 document-plan))
       :blockly/value–2         (prepare-document-plan (:value2 document-plan))
       :blockly/dictionary-item (prepare-document-plan (:dictionaryItem document-plan))})))

(defn transact-item [conn key data-item]
  (let [current-ts (utils/ts-now)]
    @(d/transact conn [(remove-nil-vals
                         {:document-plan/id              key
                          :document-plan/uid             (:uid data-item)
                          :document-plan/data-sample-id  (:dataSampleId data-item)
                          :document-plan/name            (:name data-item)
                          :document-plan/kind            (:kind data-item)
                          :document-plan/blockly-xml     (:blocklyXml data-item)
                          :document-plan/document-plan   (prepare-document-plan (:documentPlan data-item))
                          :document-plan/created-at      current-ts
                          :document-plan/updated-at      current-ts
                          :document-plan/data-sample-row (:dataSampleRow data-item)
                          :document-plan/update-count    0})])
    (assoc data-item
      :id key
      :createdAt current-ts
      :updatedAt current-ts
      :updateCount 0)))

(defn doc-plan->document-plan [document-plan]
  (when document-plan
    (remove-empty-or-nil-but-not-nil-list-vals
      {:segments       (map doc-plan->document-plan (:blockly/segments document-plan))
       :children       (if (and (:blockly/hasChildren document-plan)
                                (nil? (:blockly/children document-plan)))
                         (if (= "theme" (:blockly/name document-plan))
                           [nil]
                           [])
                         (map doc-plan->document-plan (:blockly/children document-plan)))
       :conditions     (map doc-plan->document-plan (:blockly/conditions document-plan))
       :conceptId      (:blockly/concept-id document-plan)
       :kind           (:blockly/kind document-plan)
       :srcId          (:blockly/srcId document-plan)
       :type           (:blockly/type document-plan)
       :label          (:blockly/label document-plan)
       :name           (:blockly/name document-plan)
       :text           (:blockly/text document-plan)
       :itemId         (:blockly/item-id document-plan)
       :operator       (:blockly/operator document-plan)
       :child          (doc-plan->document-plan (:blockly/child document-plan))
       :condition      (doc-plan->document-plan (:blockly/condition document-plan))
       :thenExpression (doc-plan->document-plan (:blockly/then-expression document-plan))
       :list           (doc-plan->document-plan (:blockly/list document-plan))
       :modifier       (doc-plan->document-plan (:blockly/modifier document-plan))
       :value          (doc-plan->document-plan (:blockly/value document-plan))
       :value1         (doc-plan->document-plan (:blockly/value-1 document-plan))
       :value2         (doc-plan->document-plan (:blockly/value–2 document-plan))
       :roles          (map doc-plan->document-plan (:blockly/roles document-plan))
       :dictionaryItem (doc-plan->document-plan (:blockly/dictionary-item document-plan))})))

(defn dp->dp [document-plan]
  {:id            (:document-plan/id document-plan)
   :uid           (:document-plan/uid document-plan)
   :name          (:document-plan/name document-plan)
   :kind          (:document-plan/kind document-plan)
   :blocklyXml    (:document-plan/blockly-xml document-plan)
   :documentPlan  (doc-plan->document-plan (:document-plan/document-plan document-plan))
   :createdAt     (:document-plan/created-at document-plan)
   :updatedAt     (:document-plan/updated-at document-plan)
   :dataSampleRow (:document-plan/data-sample-row document-plan)
   :dataSampleId  (:document-plan/data-sample-id document-plan)
   :updateCount   (:document-plan/update-count document-plan)})

(defn pull-entity [conn key]
  (let [document-plan (ffirst (d/q '[:find (pull ?e [*])
                                     :in $ ?key
                                     :where [?e :document-plan/id ?key]]
                                   (d/db conn)
                                   key))]
    (when document-plan
      (remove-nil-vals (dp->dp document-plan)))))

(defn scan [conn]
  (let [resp (map first (d/q '[:find (pull ?e [*])
                               :where [?e :document-plan/id]]
                             (d/db conn)))]
    (map (fn [document-plan] (dp->dp document-plan)) resp)))

(defn update! [conn key data-item]
  (let [original (pull-entity conn key)
        current-ts (utils/ts-now)]
    @(d/transact conn [(remove-nil-vals
                         {:db/id                         [:document-plan/id key]
                          :document-plan/uid             (:uid data-item)
                          :document-plan/data-sample-id  (:dataSampleId data-item)
                          :document-plan/name            (:name data-item)
                          :document-plan/kind            (:kind data-item)
                          :document-plan/blockly-xml     (:blocklyXml data-item)
                          :document-plan/document-plan   (prepare-document-plan (:documentPlan data-item))
                          :document-plan/updated-at      current-ts
                          :document-plan/data-sample-row (:dataSampleRow data-item)
                          :document-plan/update-count    (if (some? (:updateCount original))
                                                           (inc (:updateCount original))
                                                           0)})])
    (pull-entity conn key)))

(defn delete [conn key]
  @(d/transact conn [[:db.fn/retractEntity [:document-plan/id key]]])
  nil)
