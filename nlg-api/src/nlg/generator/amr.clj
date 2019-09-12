(ns nlg.generator.amr
  (:require [clojure.tools.logging :as log]
            [ccg-kit.verbnet.ccg :as vn-ccg]
            [clojure.string :as str]
            [ccg-kit.grammar :as ccg]))

(defn to-placeholder
  [k]
  (format "{{%s}}" (str/upper-case k)))

(defn build-restrictors
  [vc amr->csv-key]
  (map (fn [f]
         (let [restrict (filter #(contains? % :restrictors) (:syntax f))]
           (if (seq restrict)
             (fn [data]
               (every? (fn [[restrictors v]] ;; If for every part in rule ...
                         (every? (fn [r] ;; ... every restrictor passes
                                   (log/debugf "Restrictor: %s key: %s (%s)" r v (get data v))
                                   (case (:type r)
                                     :count (case (:value r)
                                              :singular (not (str/includes? (get data v) ","))
                                              :plural (str/includes? (get data v) ","))
                                     true)) ;; Ignore all other types for now
                                 restrictors))
                       (map
                        (fn [pattern]
                          [(:restrictors pattern) (get amr->csv-key (str/upper-case (:value pattern)))])
                        restrict)))
             (fn [_] true)))) ;; If no restrictors, just always return true)
       (:frames vc))) ;; Hardcoded single case for now.)

(defn build-grammars
  [vc members]
  (-> vc
      (assoc :members members)
      (vn-ccg/vn->grammar)))

(defn placeholders->dyn-names
  [children]
  (into {}
        (map (fn [c]
               (let [title (get-in c [:attrs :title])
                     dyn-name (get-in c [:name :dyn-name])]
                 [(to-placeholder title) dyn-name]))
             (->> children
                  (map :dynamic)
                  (flatten)))))

(defn amr-keys->data-keys [children]
  "AMR-Key is key used inside, eg. `Agent` Data-key is our linked CSV column, eg. `:actor`" 
  (into {}
        (map (fn [{:keys [name attrs]}]
               (case (:source attrs)
                 :cell [(str/upper-case (:title attrs)) (:cell name)]
                 name))
             (flatten (map :dynamic children)))))

(defn generate-results
  [grammars seed]
  (flatten
   (map
    (fn [g] (apply (partial ccg/generate g) seed))
    grammars)))
