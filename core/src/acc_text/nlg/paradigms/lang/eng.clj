(ns acc-text.nlg.paradigms.lang.eng
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.set :as set]
            [clojure.string :as str])
  (:import (java.util UUID)))

(def module "ParadigmsEng")

(defn gen-id []
  (str (UUID/randomUUID)))

(defn get-label [name args category]
  (format "%s.%s/%s" module name (str/join "->" (conj (vec args) category))))

(defn find-root [{::sg/keys [concepts relations]}]
  (first (set/difference (set (map :id concepts)) (map :to relations))))

(defmulti resolve-dict-item ::dict-item/category)

(defmethod resolve-dict-item "N" [{::dict-item/keys [key forms attributes]}]
  (let [concept (gen-id)
        noun-concept (gen-id)
        gender-concept (gen-id)
        gender (get attributes "Gender" "nonhuman")
        arity (count forms)
        args (take arity (repeatedly #(gen-id)))]
    #::sg{:id        key
          :concepts  (concat
                       [{:id       concept
                         :type     :operation
                         :name     "mkN"
                         :label    (get-label "mkN" ["Gender" "N"] "N")
                         :category "N"
                         :module   module}
                        {:id       gender-concept
                         :type     :operation
                         :name     gender
                         :label    (get-label gender [] "Gender")
                         :category "Gender"
                         :module   module}
                        {:id       noun-concept
                         :type     :operation
                         :name     "mkN"
                         :label    (get-label "mkN" (take arity (repeat "Str")) "N")
                         :category "N"
                         :module   module}]
                       (map (fn [arg form]
                              {:id       arg
                               :type     :quote
                               :value    form
                               :category "Str"})
                            args
                            forms))
          :relations (concat
                       [{:from     concept
                         :to       gender-concept
                         :role     :arg
                         :index    0
                         :category "Gender"
                         :name     "Gender"}
                        {:from     concept
                         :to       noun-concept
                         :role     :arg
                         :index    1
                         :category "N"
                         :name     "N"}]
                       (map-indexed (fn [i arg]
                                      {:from     noun-concept
                                       :to       arg
                                       :role     :arg
                                       :index    i
                                       :category "Str"
                                       :name     "Str"})
                                    args))}))

(defmethod resolve-dict-item "PN" [dict-item]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "N"))
        concept (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkPN"
                                       :label    (get-label "mkPN" ["N"] "PN")
                                       :category "PN"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "N"
                                        :name     "N"}]))))

(defmethod resolve-dict-item "N2" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "N"))
        concept (gen-id)
        arg (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkN2"
                                       :label    (get-label "mkN2" ["N" "Str"] "N2")
                                       :category "N2"
                                       :module   module}
                                      {:id       arg
                                       :type     :quote
                                       :value    (get attributes "Post" "to")
                                       :category "Str"}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "N"
                                        :name     "N"}
                                       {:from     concept
                                        :to       arg
                                        :role     :arg
                                        :index    1
                                        :category "Str"
                                        :name     "Str"}]))))

(defmethod resolve-dict-item "N3" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "N"))
        concept (gen-id)
        prep-concept (gen-id)
        post-concept (gen-id)
        prep-arg (gen-id)
        post-arg (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkN3"
                                       :label    (get-label "mkN3" ["N" "Prep" "Prep"] "N3")
                                       :category "N3"
                                       :module   module}
                                      {:id       prep-concept
                                       :type     :operation
                                       :name     "mkPrep"
                                       :label    (get-label "mkPrep" ["Str"] "Prep")
                                       :category "Prep"
                                       :module   module}
                                      {:id       post-concept
                                       :type     :operation
                                       :name     "mkPrep"
                                       :label    (get-label "mkPrep" ["Str"] "Prep")
                                       :category "Prep"
                                       :module   module}
                                      {:id       prep-arg
                                       :type     :quote
                                       :value    (get attributes "Prep" "from")
                                       :category "Str"}
                                      {:id       post-arg
                                       :type     :quote
                                       :value    (get attributes "Post" "to")
                                       :category "Str"}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "N"
                                        :name     "N"}
                                       {:from     concept
                                        :to       prep-concept
                                        :role     :arg
                                        :index    1
                                        :category "Prep"
                                        :name     "Prep"}
                                       {:from     concept
                                        :to       post-concept
                                        :role     :arg
                                        :index    2
                                        :category "Prep"
                                        :name     "Prep"}
                                       {:from     prep-concept
                                        :to       prep-arg
                                        :role     :arg
                                        :index    0
                                        :category "Str"
                                        :name     "Str"}
                                       {:from     post-concept
                                        :to       post-arg
                                        :role     :arg
                                        :index    0
                                        :category "Str"
                                        :name     "Str"}]))))

(defmethod resolve-dict-item "Prep" [{::dict-item/keys [key forms]}]
  (let [concept (gen-id)
        arg (gen-id)]
    #::sg{:id        key
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkPrep"
                       :label    (get-label "mkPrep" ["Str"] "Prep")
                       :category "Prep"
                       :module   module}
                      {:id       arg
                       :type     :quote
                       :value    (first forms)
                       :category "Str"}]
          :relations [{:from     concept
                       :to       arg
                       :role     :arg
                       :index    0
                       :category "Str"
                       :name     "Str"}]}))

(defmethod resolve-dict-item "Post" [{::dict-item/keys [key forms]}]
  (let [concept (gen-id)
        arg (gen-id)]
    #::sg{:id        key
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkPost"
                       :label    (get-label "mkPost" ["Str"] "Prep")
                       :category "Prep"
                       :module   module}
                      {:id       arg
                       :type     :quote
                       :value    (first forms)
                       :category "Str"}]
          :relations [{:from     concept
                       :to       arg
                       :role     :arg
                       :index    0
                       :category "Str"
                       :name     "Str"}]}))

(defmethod resolve-dict-item "Pron" [{::dict-item/keys [key forms attributes]}]
  (let [concept (gen-id)
        arity (count forms)
        args (take arity (repeatedly #(gen-id)))
        arg-types (concat (take arity (repeat "Str")) ["Number" "Person" "Gender"])
        number-concept (gen-id)
        number (get attributes "Number" "singular")
        person-concept (gen-id)
        person (get attributes "Person" "P3")
        gender-concept (gen-id)
        gender (get attributes "Gender" "nonhuman")]
    #::sg{:id        key
          :concepts  (cons
                       {:id       concept
                        :type     :operation
                        :name     "mkPron"
                        :label    (get-label "mkPron" arg-types "Pron")
                        :category "Pron"
                        :module   module}
                       (concat
                         (map (fn [arg form]
                                {:id       arg
                                 :type     :quote
                                 :value    form
                                 :category "Str"})
                              args
                              forms)
                         [{:id       number-concept
                           :type     :operation
                           :name     number
                           :label    (get-label number [] "Number")
                           :category "Number"
                           :module   module}
                          {:id       person-concept
                           :type     :operation
                           :name     person
                           :label    (get-label person [] "Person")
                           :category "Person"
                           :module   module}
                          {:id       gender-concept
                           :type     :operation
                           :name     gender
                           :label    (get-label gender [] "Gender")
                           :category "Gender"
                           :module   module}]))
          :relations (concat
                       (map-indexed (fn [i arg]
                                      {:from     concept
                                       :to       arg
                                       :role     :arg
                                       :index    i
                                       :category "Str"
                                       :name     "Str"})
                                    args)
                       [{:from     concept
                         :to       number-concept
                         :role     :arg
                         :index    (+ arity 1)
                         :category "Number"
                         :name     "Number"}
                        {:from     concept
                         :to       person-concept
                         :role     :arg
                         :index    (+ arity 2)
                         :category "Person"
                         :name     "Person"}
                        {:from     concept
                         :to       gender-concept
                         :role     :arg
                         :index    (+ arity 3)
                         :category "Gender"
                         :name     "Gender"}])}))

(defmethod resolve-dict-item "Quant" [{::dict-item/keys [key forms]}]
  (let [concept (gen-id)
        arity (count forms)
        args (take arity (repeatedly #(gen-id)))]
    #::sg{:id        key
          :concepts  (cons
                       {:id       concept
                        :type     :operation
                        :name     "mkQuant"
                        :label    (get-label "mkQuant" (take arity (repeat "Str")) "Quant")
                        :category "Quant"
                        :module   module}
                       (concat
                         (map (fn [arg form]
                                {:id       arg
                                 :type     :quote
                                 :value    form
                                 :category "Str"})
                              args
                              forms)))
          :relations (map-indexed (fn [i arg]
                                    {:from     concept
                                     :to       arg
                                     :role     :arg
                                     :index    i
                                     :category "Str"
                                     :name     "Str"})
                                  args)}))

(defmethod resolve-dict-item "Subj" [{::dict-item/keys [key forms]}]
  (let [concept (gen-id)
        arg (gen-id)]
    #::sg{:id        key
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkSubj"
                       :label    (get-label "mkSubj" ["Str"] "Subj")
                       :category "Subj"
                       :module   module}
                      {:id       arg
                       :type     :quote
                       :value    (first forms)
                       :category "Str"}]
          :relations [{:from     concept
                       :to       arg
                       :role     :arg
                       :index    0
                       :category "Str"
                       :name     "Str"}]}))

(defmethod resolve-dict-item "Interj" [{::dict-item/keys [key forms]}]
  (let [concept (gen-id)
        arg (gen-id)]
    #::sg{:id        key
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkInterj"
                       :label    (get-label "mkInterj" ["Str"] "Interj")
                       :category "Interj"
                       :module   module}
                      {:id       arg
                       :type     :quote
                       :value    (first forms)
                       :category "Str"}]
          :relations [{:from     concept
                       :to       arg
                       :role     :arg
                       :index    0
                       :category "Str"
                       :name     "Str"}]}))

(defmethod resolve-dict-item "V" [{::dict-item/keys [key forms]}]
  (let [concept (gen-id)
        arity (count forms)
        args (take arity (repeatedly #(gen-id)))]
    #::sg{:id        key
          :concepts  (cons
                       {:id       concept
                        :type     :operation
                        :name     "mkV"
                        :label    (get-label "mkV" (take arity (repeat "Str")) "V")
                        :category "V"
                        :module   module}
                       (map (fn [arg form]
                              {:id       arg
                               :type     :quote
                               :value    form
                               :category "Str"})
                            args
                            forms))
          :relations (map-indexed (fn [i arg]
                                    {:from     concept
                                     :to       arg
                                     :role     :arg
                                     :index    i
                                     :category "Str"
                                     :name     "Str"})
                                  args)}))

(defmethod resolve-dict-item "V2" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)
        arg (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkV2"
                                       :label    (get-label "mkV2" ["V" "Str"] "V2")
                                       :category "V2"
                                       :module   module}
                                      {:id       arg
                                       :type     :quote
                                       :value    (get attributes "Post" "in")
                                       :category "Str"}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}
                                       {:from     concept
                                        :to       arg
                                        :role     :arg
                                        :index    1
                                        :category "Str"
                                        :name     "Str"}]))))

(defmethod resolve-dict-item "V3" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)
        prep-concept (gen-id)
        post-concept (gen-id)
        prep-arg (gen-id)
        post-arg (gen-id)
        prep-attribute (get attributes "Prep")
        post-attribute (get attributes "Post")
        arity (cond-> 1
                      (some? prep-attribute) (inc)
                      (and (some? prep-attribute) (some? post-attribute)) (inc))
        arg-types (case arity
                    1 ["V"]
                    2 ["V" "Prep"]
                    3 ["V" "Prep" "Prep"])]
    (-> child
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV3"
                  :label    (get-label "mkV3" arg-types "V3")
                  :category "V3"
                  :module   module}]
                (when (< 1 arity)
                  [{:id       prep-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (get-label "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       prep-arg
                    :type     :quote
                    :value    prep-attribute
                    :category "Str"}])
                (when (< 2 arity)
                  [{:id       post-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (get-label "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       post-arg
                    :type     :quote
                    :value    post-attribute
                    :category "Str"}]))
        (update ::sg/relations concat
                [{:from     concept
                  :to       (find-root child)
                  :role     :arg
                  :index    0
                  :category "V"
                  :name     "V"}]
                (when (< 1 arity)
                  [{:from     concept
                    :to       prep-concept
                    :role     :arg
                    :index    1
                    :category "Prep"
                    :name     "Prep"}
                   {:from     prep-concept
                    :to       prep-arg
                    :role     :arg
                    :index    0
                    :category "Str"
                    :name     "Str"}])
                (when (< 2 arity)
                  [{:from     concept
                    :to       post-concept
                    :role     :arg
                    :index    2
                    :category "Prep"
                    :name     "Prep"}
                   {:from     post-concept
                    :to       post-arg
                    :role     :arg
                    :index    0
                    :category "Str"
                    :name     "Str"}])))))

(defmethod resolve-dict-item "VA" [dict-item]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVA"
                                       :label    (get-label "mkVA" ["V"] "VA")
                                       :category "VA"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2A" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)
        prep-concept (gen-id)
        post-concept (gen-id)
        prep-arg (gen-id)
        post-arg (gen-id)
        prep-attribute (get attributes "Prep")
        post-attribute (get attributes "Post")
        arity (cond-> 1
                      (some? prep-attribute) (inc)
                      (and (some? prep-attribute) (some? post-attribute)) (inc))
        arg-types (case arity
                    1 ["V"]
                    2 ["V" "Prep"]
                    3 ["V" "Prep" "Prep"])]
    (-> child
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2A"
                  :label    (get-label "mkV2A" arg-types "V2A")
                  :category "V2A"
                  :module   module}]
                (when (< 1 arity)
                  [{:id       prep-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (get-label "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       prep-arg
                    :type     :quote
                    :value    prep-attribute
                    :category "Str"}])
                (when (< 2 arity)
                  [{:id       post-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (get-label "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       post-arg
                    :type     :quote
                    :value    post-attribute
                    :category "Str"}]))
        (update ::sg/relations concat
                [{:from     concept
                  :to       (find-root child)
                  :role     :arg
                  :index    0
                  :category "V"
                  :name     "V"}]
                (when (< 1 arity)
                  [{:from     concept
                    :to       prep-concept
                    :role     :arg
                    :index    1
                    :category "Prep"
                    :name     "Prep"}
                   {:from     prep-concept
                    :to       prep-arg
                    :role     :arg
                    :index    0
                    :category "Str"
                    :name     "Str"}])
                (when (< 2 arity)
                  [{:from     concept
                    :to       post-concept
                    :role     :arg
                    :index    2
                    :category "Prep"
                    :name     "Prep"}
                   {:from     post-concept
                    :to       post-arg
                    :role     :arg
                    :index    0
                    :category "Str"
                    :name     "Str"}])))))

(defmethod resolve-dict-item "VQ" [dict-item]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVQ"
                                       :label    (get-label "mkVQ" ["V"] "VQ")
                                       :category "VQ"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2Q" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)
        prep-concept (gen-id)
        prep-arg (gen-id)
        prep-attribute (get attributes "Prep" "as")]
    (-> child
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2Q"
                  :label    (get-label "mkV2Q" ["V" "Prep"] "V2Q")
                  :category "V2Q"
                  :module   module}
                 {:id       prep-concept
                  :type     :operation
                  :name     "mkPrep"
                  :label    (get-label "mkPrep" ["Str"] "Prep")
                  :category "Prep"
                  :module   module}
                 {:id       prep-arg
                  :type     :quote
                  :value    prep-attribute
                  :category "Str"}])
        (update ::sg/relations concat
                [{:from     concept
                  :to       (find-root child)
                  :role     :arg
                  :index    0
                  :category "V"
                  :name     "V"}
                 {:from     concept
                  :to       prep-concept
                  :role     :arg
                  :index    1
                  :category "Prep"
                  :name     "Prep"}
                 {:from     prep-concept
                  :to       prep-arg
                  :role     :arg
                  :index    0
                  :category "Str"
                  :name     "Str"}]))))

(defmethod resolve-dict-item "VS" [dict-item]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVS"
                                       :label    (get-label "mkVS" ["V"] "VS")
                                       :category "VS"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2S" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)
        prep-concept (gen-id)
        prep-arg (gen-id)
        prep-attribute (get attributes "Prep" "as")]
    (-> child
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2S"
                  :label    (get-label "mkV2S" ["V" "Prep"] "V2Q")
                  :category "V2S"
                  :module   module}
                 {:id       prep-concept
                  :type     :operation
                  :name     "mkPrep"
                  :label    (get-label "mkPrep" ["Str"] "Prep")
                  :category "Prep"
                  :module   module}
                 {:id       prep-arg
                  :type     :quote
                  :value    prep-attribute
                  :category "Str"}])
        (update ::sg/relations concat
                [{:from     concept
                  :to       (find-root child)
                  :role     :arg
                  :index    0
                  :category "V"
                  :name     "V"}
                 {:from     concept
                  :to       prep-concept
                  :role     :arg
                  :index    1
                  :category "Prep"
                  :name     "Prep"}
                 {:from     prep-concept
                  :to       prep-arg
                  :role     :arg
                  :index    0
                  :category "Str"
                  :name     "Str"}]))))

(defmethod resolve-dict-item "VV" [dict-item]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)]
    (-> child
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVV"
                                       :label    (get-label "mkVV" ["V"] "VV")
                                       :category "VV"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2V" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (gen-id)
        prep-concept (gen-id)
        post-concept (gen-id)
        prep-arg (gen-id)
        post-arg (gen-id)
        prep-attribute (get attributes "Prep")
        post-attribute (get attributes "Post")
        arity (cond-> 1 (and (some? prep-attribute) (some? post-attribute)) (+ 2))
        arg-types (case arity
                    1 ["V"]
                    3 ["V" "Prep" "Prep"])]
    (-> child
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2V"
                  :label    (get-label "mkV2V" arg-types "V2V")
                  :category "V2V"
                  :module   module}]
                (when (< 1 arity)
                  [{:id       prep-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (get-label "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       prep-arg
                    :type     :quote
                    :value    prep-attribute
                    :category "Str"}])
                (when (< 2 arity)
                  [{:id       post-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (get-label "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       post-arg
                    :type     :quote
                    :value    post-attribute
                    :category "Str"}]))
        (update ::sg/relations concat
                [{:from     concept
                  :to       (find-root child)
                  :role     :arg
                  :index    0
                  :category "V"
                  :name     "V"}]
                (when (< 1 arity)
                  [{:from     concept
                    :to       prep-concept
                    :role     :arg
                    :index    1
                    :category "Prep"
                    :name     "Prep"}
                   {:from     prep-concept
                    :to       prep-arg
                    :role     :arg
                    :index    0
                    :category "Str"
                    :name     "Str"}])
                (when (< 2 arity)
                  [{:from     concept
                    :to       post-concept
                    :role     :arg
                    :index    2
                    :category "Prep"
                    :name     "Prep"}
                   {:from     post-concept
                    :to       post-arg
                    :role     :arg
                    :index    0
                    :category "Str"
                    :name     "Str"}])))))
