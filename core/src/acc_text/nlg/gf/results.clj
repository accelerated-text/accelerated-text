(ns acc-text.nlg.gf.results
  (:require [clojure.string :as str]
            [clojure.zip :as zip]))

(defn get-attrs [cat]
  (when (some? cat)
    (let [[_ type position] (re-find #"(.+?)(\d+)" cat)]
      {:type     (keyword (str/replace type #"_" "-"))
       :position (Integer/parseInt position)})))

(defn make-concept [{:keys [cat text]} lincat]
  (if (some? cat)
    (cond-> (get-attrs cat)
      (contains? lincat cat) (assoc :category (get lincat cat)))
    {:type     :quote
     :value    text
     :category "Str"}))

(defn make-zipper [t]
  (zip/zipper
   map?
   (fn [node]
     (:children node))
   (fn [node children]
     (assoc node :children children))
   t))

(defn get-concepts [tree lincat]
  (->> (make-zipper tree)
       (iterate zip/next)
       (take-while #(not (zip/end? %)))
       (map (comp
             #(make-concept (cond->> %
                              (string? %) (hash-map :text))
                            lincat)
             zip/node))))

(defn emend [text]
  (->> (str/split text #"\s*¶+\s*")
       (map (fn [paragraph]
              (-> paragraph
                  (str/replace #"^\p{Ll}" str/capitalize)
                  (str/replace #"\s+[,.?!]" str/trim)
                  (str/replace #"([.?!]\s+)(\p{Ll})" #(str (nth % 1) (str/capitalize (nth % 2))))
                  (str/replace #"[^.?!]$" #(str % ".")))))
       (str/join "\n")))

(defn bind [text]
  (str/replace text #"\s*\<pgf\.BIND\>\s*" ""))

(defn post-process [lang lincat {[tree] :tree}]
  (let [concepts (get-concepts tree lincat)]
    {:text     (->> concepts (map :value) (remove nil?) (str/join " ") (bind) (emend))
     :concepts concepts
     :language lang}))
