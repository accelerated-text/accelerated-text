(ns lt.tokenmill.nlg.generator.planner)

;; Lang functions
(defn set-subj [selector]  (fn [context data] (assoc context :subj (selector data))))
(defn set-verb-w-selector [selector] (fn [context data] (assoc context :verb (selector data))))
(defn set-verb-static [verb] (fn [context _] (assoc context :verb verb)))
(defn set-obj [selector] (fn [context data] (update context :objs (fn [vals] (conj vals (selector data))))))

(defn compile-attribute-selector
  [value]
  (let [attr-name (value :attribute)]
    (fn [data]
      (get data attr-name))))

(defn compile-static-seq
  [value]
  (let [selectors (map compile-attribute-selector (value :attributes))]
    (map #(set-obj %) selectors)))

(defn compile-single
  [value]
  (let [selector (compile-attribute-selector value)]
    (set-obj selector)))

(defn compile-purpose
  [purpose]
  (let [rel-name (purpose :relationship)
        value (purpose :value)
        type (value :type)
        children (case type
                   "Attribute" (list (compile-single value))
                   "All" (compile-static-seq value))]
    (conj children (set-verb-static rel-name))))

(defn compile-purposes
  [purposes]
  (when (not (nil? purposes))
    (map compile-purpose purposes)))

(defn compile-component
  [component]
  (let [purposes  (compile-purposes (component :purposes))]
    (concat (list
             (set-subj
              (compile-attribute-selector (component :name))))
            (flatten purposes))))

(defn compile-dp
  [document-plan]
  (let [items (document-plan :items)]
    (doall (map compile-component items))))


(defn build-dp-instance [dp data]
  (loop [context {:subj nil
                  :objs []
                  :verb nil}
         fs dp]
    (if (empty? fs)
      context
      (let [head (first fs)
            tail (rest fs)
            result (head context data)]
        (recur (merge context result) tail)))))
