(ns acc-text.nlg.document-plan.instances)

(defmulti add-context (fn [concept context] (get concept :acctext.amr/type)))

(defmethod add-context :default [concept context] concept)

(defmethod add-context :data [{value :acctext.amr/value :as concept} {data :data}]
  (update concept :acctext.amr/context #(assoc % :acctext.amr/value (get data value value))))

(defmethod add-context :dictionary-item [{value :acctext.amr/value :as concept} {dictionary :dictionary}]
  (update concept :acctext.amr/context #(assoc % :acctext.amr/value (get dictionary value value))))

(defn generate-instance [document-plan context]
  (update document-plan :acctext.amr/concepts #(mapv (fn [concept] (add-context concept context)) %)))
