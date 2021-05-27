(ns dealer-api.core
  (:require [dealer-api.drugs :as d]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :refer [body-params]]))


(defn respond-hello [_]
  {:status 200
   :body "Hello World"})

(def routes
  #{["/hello" :get `respond-hello]
    ["/drugs" :get d/all-drugs :route-name :get-drugs]
    ["/drugs" :post [(body-params) d/create-drug] :route-name :post-drug]
    ["/drugs" :put [(body-params) d/update-drug] :route-name :put-drug]

    ;; Caused by: java.lang.ClassCastException: java.lang.Integer cannot be cast to clojure.lang.Associative \/
    ["/drugs" :delete [(body-params) d/delete-drug] :route-name :delete-drug]})

(def service-map
  {::http/routes routes
   ::http/port 8890
   ::http/type :jetty})

;; reminder! :reload-all is a thing

(comment
  (do
    (require '[dealer-api.config :refer [db]])
    (require '[dealer-api.sql.drugs :as sd] :reload)))

