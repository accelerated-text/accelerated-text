(ns acc-text.nlg.gf.utils
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :as str]))

(defn get-rgl-functions-from-resource [& paths]
  (->> paths
       (mapcat #(str/split (slurp %) #"\n\n"))
       (map #(let [[desc & items] (str/split-lines (str/trim %))
                   [_ type text] (re-find #"//\s+(.+?)\s+-\s+(.+)" desc)]
               {:type        (or type (second (re-find #"//\s+(.+)" desc)))
                :description (or text "")
                :functions   (mapv (fn [line]
                                     (let [name (second (re-find #"^([\w()]+)" line))
                                           line (str/trim (subs line (count name)))
                                           body (str/trim (second (re-find #"([\w()]+ (\s*-> [\w()]+)*)" line)))
                                           type (mapv str/trim (str/split body #"\s+->\s+"))
                                           line (str/trim (subs line (count body)))]
                                       {:function name
                                        :type     type
                                        :example  line}))
                                   items)}))
       (group-by :type)
       (vals)
       (map (fn [instances]
              (let [[{:keys [type description]}] instances]
                {:type        type
                 :description description
                 :module      "Syntax"
                 :functions   (into [] (mapcat :functions instances))})))
       (sort-by :type)
       (into [])))

(defn spit-rgl-functions [fns output-path]
  (doseq [{type :type :as f} fns]
    (spit (format (str output-path "/%s.edn") type) (with-out-str (pprint f)))))

