(ns acc-text.nlg.paradigms.lang.eng
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.paradigms.utils :as utils]
            [acc-text.nlg.semantic-graph :as sg]))

(def module "ParadigmsEng")

(defmulti resolve-dict-item ::dict-item/category)

(defmethod resolve-dict-item "N" [{::dict-item/keys [key forms attributes]}]
  (let [concept (utils/gen-id)
        noun-concept (utils/gen-id)
        gender-concept (utils/gen-id)
        gender (get attributes "Gender" "nonhuman")
        arity (count forms)
        args (take arity (repeatedly #(utils/gen-id)))]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "N"
          :concepts  (concat
                       [{:id       concept
                         :type     :operation
                         :name     "mkN"
                         :label    (utils/get-label module "mkN" ["Gender" "N"] "N")
                         :category "N"
                         :module   module}
                        {:id       gender-concept
                         :type     :operation
                         :name     gender
                         :label    (utils/get-label module gender [] "Gender")
                         :category "Gender"
                         :module   module}
                        {:id       noun-concept
                         :type     :operation
                         :name     "mkN"
                         :label    (utils/get-label module "mkN" (take arity (repeat "Str")) "N")
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
        concept (utils/gen-id)]
    (-> child
        (assoc ::sg/category "PN")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkPN"
                                       :label    (utils/get-label module "mkPN" ["N"] "PN")
                                       :category "PN"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (utils/find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "N"
                                        :name     "N"}]))))

(defmethod resolve-dict-item "N2" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "N"))
        concept (utils/gen-id)
        arg (utils/gen-id)]
    (-> child
        (assoc ::sg/category "N2")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkN2"
                                       :label    (utils/get-label module "mkN2" ["N" "Str"] "N2")
                                       :category "N2"
                                       :module   module}
                                      {:id       arg
                                       :type     :quote
                                       :value    (get attributes "Post" "to")
                                       :category "Str"}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (utils/find-root child)
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
        concept (utils/gen-id)
        prep-concept (utils/gen-id)
        post-concept (utils/gen-id)
        prep-arg (utils/gen-id)
        post-arg (utils/gen-id)]
    (-> child
        (assoc ::sg/category "N3")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkN3"
                                       :label    (utils/get-label module "mkN3" ["N" "Prep" "Prep"] "N3")
                                       :category "N3"
                                       :module   module}
                                      {:id       prep-concept
                                       :type     :operation
                                       :name     "mkPrep"
                                       :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
                                       :category "Prep"
                                       :module   module}
                                      {:id       post-concept
                                       :type     :operation
                                       :name     "mkPrep"
                                       :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
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
                                        :to       (utils/find-root child)
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
  (let [concept (utils/gen-id)
        arg (utils/gen-id)]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "Prep"
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkPrep"
                       :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
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
  (let [concept (utils/gen-id)
        arg (utils/gen-id)]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "Prep"
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkPost"
                       :label    (utils/get-label module "mkPost" ["Str"] "Prep")
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
  (let [concept (utils/gen-id)
        arity (count forms)
        args (take arity (repeatedly #(utils/gen-id)))
        arg-types (concat (take arity (repeat "Str")) ["Number" "Person" "Gender"])
        number-concept (utils/gen-id)
        number (get attributes "Number" "singular")
        person-concept (utils/gen-id)
        person (get attributes "Person" "P3")
        gender-concept (utils/gen-id)
        gender (get attributes "Gender" "nonhuman")]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "Pron"
          :concepts  (cons
                       {:id       concept
                        :type     :operation
                        :name     "mkPron"
                        :label    (utils/get-label module "mkPron" arg-types "Pron")
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
                           :label    (utils/get-label module number [] "Number")
                           :category "Number"
                           :module   module}
                          {:id       person-concept
                           :type     :operation
                           :name     person
                           :label    (utils/get-label module person [] "Person")
                           :category "Person"
                           :module   module}
                          {:id       gender-concept
                           :type     :operation
                           :name     gender
                           :label    (utils/get-label module gender [] "Gender")
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
  (let [concept (utils/gen-id)
        arity (count forms)
        args (take arity (repeatedly #(utils/gen-id)))]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "Quant"
          :concepts  (cons
                       {:id       concept
                        :type     :operation
                        :name     "mkQuant"
                        :label    (utils/get-label module "mkQuant" (take arity (repeat "Str")) "Quant")
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
  (let [concept (utils/gen-id)
        arg (utils/gen-id)]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "Subj"
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkSubj"
                       :label    (utils/get-label module "mkSubj" ["Str"] "Subj")
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
  (let [concept (utils/gen-id)
        arg (utils/gen-id)]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "Interj"
          :concepts  [{:id       concept
                       :type     :operation
                       :name     "mkInterj"
                       :label    (utils/get-label module "mkInterj" ["Str"] "Interj")
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
  (let [concept (utils/gen-id)
        arity (count forms)
        args (take arity (repeatedly #(utils/gen-id)))]
    #::sg{:id        (utils/gen-id)
          :name      key
          :category  "V"
          :concepts  (cons
                       {:id       concept
                        :type     :operation
                        :name     "mkV"
                        :label    (utils/get-label module "mkV" (take arity (repeat "Str")) "V")
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
        concept (utils/gen-id)
        arg (utils/gen-id)]
    (-> child
        (assoc ::sg/category "V2")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkV2"
                                       :label    (utils/get-label module "mkV2" ["V" "Str"] "V2")
                                       :category "V2"
                                       :module   module}
                                      {:id       arg
                                       :type     :quote
                                       :value    (get attributes "Post" "in")
                                       :category "Str"}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (utils/find-root child)
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
        concept (utils/gen-id)
        prep-concept (utils/gen-id)
        post-concept (utils/gen-id)
        prep-arg (utils/gen-id)
        post-arg (utils/gen-id)
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
        (assoc ::sg/category "V3")
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV3"
                  :label    (utils/get-label module "mkV3" arg-types "V3")
                  :category "V3"
                  :module   module}]
                (when (< 1 arity)
                  [{:id       prep-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
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
                    :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       post-arg
                    :type     :quote
                    :value    post-attribute
                    :category "Str"}]))
        (update ::sg/relations concat
                [{:from     concept
                  :to       (utils/find-root child)
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
        concept (utils/gen-id)]
    (-> child
        (assoc ::sg/category "VA")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVA"
                                       :label    (utils/get-label module "mkVA" ["V"] "VA")
                                       :category "VA"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (utils/find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2A" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (utils/gen-id)
        prep-concept (utils/gen-id)
        post-concept (utils/gen-id)
        prep-arg (utils/gen-id)
        post-arg (utils/gen-id)
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
        (assoc ::sg/category "V2A")
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2A"
                  :label    (utils/get-label module "mkV2A" arg-types "V2A")
                  :category "V2A"
                  :module   module}]
                (when (< 1 arity)
                  [{:id       prep-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
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
                    :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       post-arg
                    :type     :quote
                    :value    post-attribute
                    :category "Str"}]))
        (update ::sg/relations concat
                [{:from     concept
                  :to       (utils/find-root child)
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
        concept (utils/gen-id)]
    (-> child
        (assoc ::sg/category "VQ")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVQ"
                                       :label    (utils/get-label module "mkVQ" ["V"] "VQ")
                                       :category "VQ"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (utils/find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2Q" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (utils/gen-id)
        prep-concept (utils/gen-id)
        prep-arg (utils/gen-id)
        prep-attribute (get attributes "Prep" "as")]
    (-> child
        (assoc ::sg/category "V2Q")
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2Q"
                  :label    (utils/get-label module "mkV2Q" ["V" "Prep"] "V2Q")
                  :category "V2Q"
                  :module   module}
                 {:id       prep-concept
                  :type     :operation
                  :name     "mkPrep"
                  :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
                  :category "Prep"
                  :module   module}
                 {:id       prep-arg
                  :type     :quote
                  :value    prep-attribute
                  :category "Str"}])
        (update ::sg/relations concat
                [{:from     concept
                  :to       (utils/find-root child)
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
        concept (utils/gen-id)]
    (-> child
        (assoc ::sg/category "VS")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVS"
                                       :label    (utils/get-label module "mkVS" ["V"] "VS")
                                       :category "VS"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (utils/find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2S" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (utils/gen-id)
        prep-concept (utils/gen-id)
        prep-arg (utils/gen-id)
        prep-attribute (get attributes "Prep" "as")]
    (-> child
        (assoc ::sg/category "V2S")
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2S"
                  :label    (utils/get-label module "mkV2S" ["V" "Prep"] "V2Q")
                  :category "V2S"
                  :module   module}
                 {:id       prep-concept
                  :type     :operation
                  :name     "mkPrep"
                  :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
                  :category "Prep"
                  :module   module}
                 {:id       prep-arg
                  :type     :quote
                  :value    prep-attribute
                  :category "Str"}])
        (update ::sg/relations concat
                [{:from     concept
                  :to       (utils/find-root child)
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
        concept (utils/gen-id)]
    (-> child
        (assoc ::sg/category "VV")
        (update ::sg/concepts concat [{:id       concept
                                       :type     :operation
                                       :name     "mkVV"
                                       :label    (utils/get-label module "mkVV" ["V"] "VV")
                                       :category "VV"
                                       :module   module}])
        (update ::sg/relations concat [{:from     concept
                                        :to       (utils/find-root child)
                                        :role     :arg
                                        :index    0
                                        :category "V"
                                        :name     "V"}]))))

(defmethod resolve-dict-item "V2V" [{::dict-item/keys [attributes] :as dict-item}]
  (let [child (resolve-dict-item (assoc dict-item ::dict-item/category "V"))
        concept (utils/gen-id)
        prep-concept (utils/gen-id)
        post-concept (utils/gen-id)
        prep-arg (utils/gen-id)
        post-arg (utils/gen-id)
        prep-attribute (get attributes "Prep")
        post-attribute (get attributes "Post")
        arity (cond-> 1 (and (some? prep-attribute) (some? post-attribute)) (+ 2))
        arg-types (case arity
                    1 ["V"]
                    3 ["V" "Prep" "Prep"])]
    (-> child
        (assoc ::sg/category "V2V")
        (update ::sg/concepts concat
                [{:id       concept
                  :type     :operation
                  :name     "mkV2V"
                  :label    (utils/get-label module "mkV2V" arg-types "V2V")
                  :category "V2V"
                  :module   module}]
                (when (< 1 arity)
                  [{:id       prep-concept
                    :type     :operation
                    :name     "mkPrep"
                    :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
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
                    :label    (utils/get-label module "mkPrep" ["Str"] "Prep")
                    :category "Prep"
                    :module   module}
                   {:id       post-arg
                    :type     :quote
                    :value    post-attribute
                    :category "Str"}]))
        (update ::sg/relations concat
                [{:from     concept
                  :to       (utils/find-root child)
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
