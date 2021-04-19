(ns data.entities.data-files.format-coercion
  (:require [clojure.string :as string]))


(defn transpose [m] (apply mapv vector m))

(defn coerce-ints [coll]
  (if (every? #(and (string/ends-with? (str %) ".0")
                    (number? %)) coll)
    (map int coll)
    coll))

(defn coerce-data-types
  "When reading Excel files ints are converted to floats.
  There might be other similar data type mismatches.
  Detect such cases here and convert accordingly.
  Expecting `rows` to have a header row"
  [rows]
  (->> rows
       (transpose)
       (map (fn [[head & items]] (cons head (coerce-ints items))))
       (transpose)))
