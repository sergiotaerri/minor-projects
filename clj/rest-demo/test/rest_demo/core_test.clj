(ns rest-demo.core-test
  (:require [clojure.test :refer :all]
            [rest-demo.core :refer :all]
            [ring.middleware.defaults :refer [site-defaults]]))

(deftest handler-req
  (let [m (-> (assoc-in site-defaults [:params :name] "john")
              (assoc-in [:params :surname] "wick"))]
    (is (= (people-handler m)
           [{:firstname "Jose", :surname "Esoj"}
            {:firstname "John", :surname "Wick"}
            {:firstname "John", :surname "Wick"}]))))
