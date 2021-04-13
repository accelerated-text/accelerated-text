(ns data.data-files.io-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [data.entities.data-files.io :refer [coerce-ints read-xlsx]]))

(deftest int-coercion
  (is (= [1 2 3] (coerce-ints [1.0 2.0 3.0])))
  (is (= [1.0 2.0 3] (coerce-ints [1.0 2.0 3])))
  (is (= [1.0 2.0 "ab.0"] (coerce-ints [1.0 2.0 "ab.0"]))))

(deftest coerce-excel-content
  (is (= "x,y,z\na,1.0,1\nb,1.2,11\nc,1.0,111\nd,1.11,1111\n"
         (read-xlsx (io/file "test/resources/data-files/dataformatting.xlsx")))))
