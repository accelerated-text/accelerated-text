(ns lt.tokenmill.nlg.generator.planner)

(def set-subj (fn [context data] (assoc context :subj (data :name))))
(defn set-verb-w-selector [selector] (fn [context data] (assoc context :verb (selector data))))
(defn set-verb-static [verb] (fn [context _] (assoc context :verb verb)))

(defn compile-attribute [value])

(defn compile-static-seq
  [value]
  (map compile-attribute (value :items)))

(defn compile-purpose
  [purpose]
  (let [rel-name (purpose :relationship)
        value (purpose :value)
        children (case (value :type)
                   "Attribute" (compile-attribute value)
                   "All" (compile-static-seq value))]
    (conj children (set-verb-static rel-name))))

(defn compile-purposes
  [purposes]
  (when (not (nil? purposes))
    (map compile-purpose purposes)))

(defn compile-component
  [component]
  (let [type (component :type)
        purposes  (compile-purposes (component :purposes))]
    (concat (list set-subj) (flatten purposes))))

(defn compile-dp
  [document-plan]
  (let [items (document-plan :items)]
    (doall (map compile-component items))))


(defn build-dp-instance [dp data]
  (loop [context {}
         fs dp]
    (if (empty? fs)
      context
      (let [head (first fs)
            tail (rest fs)
            result (head context data)]
        (recur (merge context result) tail)))))
