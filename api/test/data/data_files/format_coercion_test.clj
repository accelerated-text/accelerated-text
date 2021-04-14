(ns data.data-files.format-coercion-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]
            [data.entities.data-files :refer [read-xlsx]]
            [data.entities.data-files.format-coercion :refer [coerce-ints]]))

(deftest int-coercion
  (is (= [1 2 3] (coerce-ints [1.0 2.0 3.0])))
  (is (= [1.0 2.0 3] (coerce-ints [1.0 2.0 3])))
  (is (= [1.0 2.0 "ab.0"] (coerce-ints [1.0 2.0 "ab.0"]))))

(deftest coerce-excel-content
  (is (= "x,y,z\na,1.0,1\nb,1.2,11\nc,1.0,111\nd,1.11,1111\n"
         (read-xlsx (io/file "test/resources/data-files/dataformatting.xlsx")))))
