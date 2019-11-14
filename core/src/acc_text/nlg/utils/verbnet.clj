(ns acc-text.nlg.utils.verbnet
  (:require [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn- ?assoc
  "Do assoc only if value is truthy. Otherwise return the same map"
  [m k v] (if (or (nil? v) (empty? v)) m (assoc m k v)))

(defn- lowercase-keys [m]
  (reduce-kv (fn [m k v] (assoc m (-> k name string/lower-case keyword) v))
             {} m))

(defn load-xml [file] (xml/parse (io/reader file)))

(defn clean-content [{content :content}] (filter map? content))

(defmulti process-section :tag)

(defn process-xml [xml]
  (map process-section (clean-content xml)))

;; SLECTION RESTRICTORS

(defmethod process-section :SELRESTR [{attrs :attrs}] (lowercase-keys attrs))

(defmethod process-section :SELRESTRS [{:keys [attrs] :as xml}]
  (-> {}
      (?assoc :logic (:logic attrs))
      (?assoc :restrictors (process-xml xml))))

;; FRAMES

(defmethod process-section :DESCRIPTION [{attrs :attrs}] {:description attrs})

(defmethod process-section :EXAMPLE [xml] (-> xml :content first))

(defmethod process-section :EXAMPLES [xml] {:examples (process-xml xml)})

(defmethod process-section :SYNRESTRS [xml]
  (map (comp lowercase-keys :attrs) (clean-content xml)))

(defmethod process-section :SYNTAX [xml]
  {:syntax (map (fn [{:keys [tag attrs] :as xml}]
                  (->  {:pos tag}
                       (?assoc :value (:value attrs))
                       (?assoc :restrictors (first (process-xml xml)))))
                (clean-content xml))})

(defmethod process-section :FRAME [xml]
  (into {} (process-xml xml)))

(defmethod process-section :FRAMES [xml]
  {:frames (process-xml xml)})

;; SEMANTICS

(defmethod process-section :ARGS [xml]
  (map :attrs (clean-content xml)))

(defmethod process-section :PRED [{:keys [attrs] :as xml}]
  (-> {:value (:value attrs)}
      (?assoc :bool (:bool attrs))
      (?assoc :arguments (first (process-xml xml)))))

(defmethod process-section :SEMANTICS [xml] {:semantics (process-xml xml)})

;; MEMEBERS

(defmethod process-section :MEMBER [{attrs :attrs}] attrs)

(defmethod process-section :MEMBERS [xml] {:members (process-xml xml)})

;; THEMATIC ROLES

(defmethod process-section :THEMROLE [{attrs :attrs :as xml}]
  (?assoc {:type (:type attrs)}
          :selection-restrictions (remove
                                   #(or (nil? %) (empty? %))
                                   (process-xml xml))))

(defmethod process-section :THEMROLES [xml] {:thematic-roles (process-xml xml)})

(defmethod process-section :default [_] nil)

(defn xml->vclass [xml-file]
  (let [vnet-xml (load-xml xml-file)]
    (into {:id (-> vnet-xml :attrs :ID)} (process-xml vnet-xml))))
