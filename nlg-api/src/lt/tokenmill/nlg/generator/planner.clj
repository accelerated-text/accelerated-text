(ns lt.tokenmill.nlg.generator.planner)

(def set-subj (fn [context data] (assoc context :subj (data :name))))

(defn compile-component
  [component]
  (let [type (component :type)]
    (list set-subj)))

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
