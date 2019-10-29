(ns acc-text.nlg.verbnet.grammar-patterns
  (:require [clojure.set :as set]
            [clojure.string :as string]
            [clojure.tools.logging :as log]))

(defn pattern [predicate a b placement]
  {:predicate predicate
   :arg1      a
   :arg2      b
   :placement placement})

(defn optimize-arguments
  "Optimizes arguments like this:
   F(a(b), c(a, d)) -> F(b, c(a, d))

   We don't want same variable to be repeated"
  [patterns]
  (letfn [(single? [{:keys [arg1 arg2]}] (and arg1 (nil? arg2)))
          (inside-predicate? [{:keys [predicate]} {:keys [arg1 arg2]}] (or (= arg1 predicate) (= arg2 predicate)))
          (predicate? [p] (contains? p :predicate))]
    (map
      (fn [{:keys [arg1 arg2] :as pattern}]
        (log/tracef "Arg: %s single? %b predicate? %b Inside? %b" arg1 (single? arg1) (predicate? arg1) (inside-predicate? arg1 arg2))
        (cond
          (and (predicate? arg1) (inside-predicate? arg1 arg2) (single? arg1)) (assoc pattern :arg1 (:arg1 arg1))
          (and (predicate? arg2) (inside-predicate? arg2 arg1) (single? arg2)) (assoc pattern :arg2 (:arg1 arg2))
          :else                                                                pattern))
      patterns)))

(defn replace-arguments
  " Go through all patterns and replace arguments with given predicate:
    a(b), F[1](a, c),  F[2](d, a) -> F[1](a(b), c), F[2](d, a(b))"
  [{:keys [predicate] :as arg} patterns]
  (remove nil?
          (map
           (fn [{:keys [arg1 arg2] :as p}]
             (cond
               (= predicate arg1) {:original p :new (assoc p :arg1 arg)}
               (= predicate arg2) {:original p :new (assoc p :arg2 arg)}
               :else nil))
           patterns)))

(defn merge-arguments
  " Merges functions like this:
    `a(x, y), F(a, b) -> F(a(x, y), b)`

    Additional step is done for such cases:
    a(x, b), F(a, b) -> F(a(x), b)

    Eg. if we have situation:
    `LEX[is](NP[x], ADJ[cool])`
    `VERB[does](LEX[something], LEX[is])`

    it would create:

    `VERB[does](LEX[something], (NP[x], ADJ[cool]))`"
  [patterns]
  (loop [[head & tail] (set patterns)
         results []
         replaced []]
    (if (nil? head)
      (set/difference
       (set (flatten results))
       (set (flatten replaced)))
      (let [replaces (replace-arguments head patterns)]
        (log/debugf "Replaces: %s" (pr-str replaces))
        (if (empty? replaces)
          (recur tail (conj results head) replaced)
          (recur tail (conj results (map :new replaces)) (concat replaced (map :original replaces))))))))

(defn permutations [s]
  (lazy-seq
   (if (seq (rest s))
     (apply concat (for [x s]
                     (map #(cons x %) (permutations (remove #{x} s)))))
     [s])))

(defn depth
  ([branch] (depth branch 0))
  ([{:keys [predicate arg1 arg2]} n]
   (if predicate
     (max (depth arg1 (inc n)) (depth arg2 (inc n)))
     n)))

(defn merge-predicates
  "Merges functions. Tries to balance both sides, so it does: left, right, left, right... merges

  Right:
  `F(a, b), F(b, c) -> F(a, b(c))`

  Left:
  `F(a, b), F(b, c) -> F(b(a), c)`

  Eg. if we have situation:
  `LEX[is](NP[x], LEX[owner])`
  `LEX[is](LEX[owner], NP[y])`

  It would create:

  `LEX[is](NP[x], LEX[owner](NP[y])`"
  [patterns]
  (letfn [(merge-left
            [p1 p2]
            (log/debug "Merging left")
            {:result (-> p2
                         (assoc :arg1 {:predicate (:arg1 p1)
                                       :arg1 (:arg1 p2)
                                       :arg2 nil
                                       :placement :end})
                         (assoc :arg2 (:arg2 p1))
                         (assoc :placement :middle))


             :p1 p1
             :p2 p2})
          (merge-right
            [p1 p2]
            (log/debug "Merging right")
            {:result (-> p1
                         (assoc :arg2 {:predicate (:arg1 p1)
                                       :arg1 (:arg2 p1)
                                       :arg2 nil
                                       :placement :start})
                         (assoc :arg1 (:arg1 p2))
                         (assoc :placement :middle))

             :p1 p1
             :p2 p2})
          (merge-pairs
            [pairs]
            (loop [[[p1 p2] & tail] pairs]
              (log/tracef "p1: %s p2: %s" p1 p2 )
              (if (and (:arg1 p1) (= (:arg1 p1) (:arg2 p2)))
                (if (> (depth (:arg2 p1)) (depth (:arg1 p2))) ;; Keep tree balanced
                  (merge-left p1 p2)
                  (merge-right p1 p2))
                (when (seq? tail)
                  (recur tail)))))
          (get-predicate [[_ v]]
            (loop [items v]
              (let [c (permutations items)
                    r (merge-pairs c)]
                (if r
                  (let [exclude (set [(:p1 r) (:p2 r)])
                        result (:result r)]
                    (log/debugf "Result: %s Exclude: %s" result (pr-str exclude))
                    (recur (vec (conj (set/difference
                                       (set items)
                                       exclude)
                                      result))))
                  items))))]
    (flatten
     (map get-predicate
          (group-by :predicate patterns)))))

(defn combine-roots
  "In case we have two root items at the end of merging, we need to combine them somehow"
  [patterns]
  (case (count patterns)
    1 patterns ;; Usually there's only one item, do nothing
    2 (let [[p1 p2] patterns]
        (cond
          (= (:arg2 p1) (:arg1 p2)) (list (assoc p1 :arg2 p2))
          (= (:arg1 p1) (:arg2 p2)) (list (assoc p2 :arg2 p1))
          :else                     (do
                                      (log/debugf "Sorry '%s' and '%s' doesn't combine" p1 p2)
                                      patterns)))
    ;; Need separate algorithm if we ever get more than two
    patterns))

(defn drop-vnet-id [word] (string/replace word #"-\d+.*" ""))

(defn role->pattern [[next-role role previous-role]]
  (case (vector (:pos previous-role) (:pos role) (:pos next-role))
    [:NP :LEX :LEX]    (pattern role previous-role next-role :middle)
    [:LEX :LEX :PREP]  (pattern next-role previous-role role :end)
    [:LEX :PREP :NP]   (pattern role previous-role next-role :middle)
    [:NP :VERB :PREP]  (pattern role previous-role next-role :middle)
    [:VERB :PREP :NP]  (pattern previous-role role next-role :start)
    [nil :NP :VERB]    (pattern next-role role nil :end)
    [:NP :LEX :VERB]   (pattern next-role previous-role role :end)
    [:LEX :VERB :PREP] (pattern role previous-role next-role :middle)
    [:LEX :LEX :LEX]   (pattern previous-role role next-role :start)
    [:LEX :LEX :NP]    (pattern previous-role role next-role :start)
    [:NP :LEX :PREP]   (pattern next-role previous-role role :end)
    [:NP :VERB :NP]    (pattern role previous-role next-role :middle)
    [:VERB :NP :PREP]  (pattern previous-role role next-role :start)
    [:NP :PREP :NP]    (pattern role previous-role next-role :middle)
    [:PREP :NP :PREP]  (pattern next-role previous-role role :end)
    [:NP :VERB :ADV]   (pattern role previous-role next-role :middle)
    [:VERB :ADV :PREP] (pattern previous-role role next-role :start)
    [:ADV :PREP :NP]   (pattern role previous-role next-role :middle)
    [:VERB :NP :NP]    (pattern previous-role role next-role :start)
    [:VERB :NP :LEX]   (pattern previous-role role next-role :start)
    [:NP :LEX :NP]     (pattern role previous-role next-role :middle)
    [:NP :NP :VERB]    (pattern next-role previous-role role :end)
    [:LEX :NP :VERB]   (pattern next-role previous-role role :end)))

(defn map-shift
  "Gives next value, current value and previous value."
  [coll]
  (->> (map vector coll (cons nil coll) (->> coll (cons nil) (cons nil)))
       (filter (fn [args] (= (min 3 (count coll)) (count (remove nil? args)))))
       (map role->pattern)))

(defn build-grammar-patterns [id syntax]
  (->> syntax
       (map (fn [p] (case   (:pos p)
                      :VERB (assoc p :value (drop-vnet-id id))
                      ;; Where do we get these adverbs?
                      :ADV  (assoc p :value (format "%s-adv" (drop-vnet-id id)))
                      p)))
       (map-shift)
       (merge-arguments)
       (merge-predicates)
       (optimize-arguments)
       (combine-roots)))
