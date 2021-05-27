(ns sergiotaerri.simple-api-server-test
  (:require [clojure.test :refer :all]
            [sergiotaerri.simple-api-server :refer :all]))

(deftest handler-test
  (testing "handler returns a sucessful request"
   (is (= 200 (:status (handler {}))))))
