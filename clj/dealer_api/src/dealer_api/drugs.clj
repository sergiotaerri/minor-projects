(ns dealer-api.drugs
  (:require [dealer-api.config :refer [db]]
            [io.pedestal.http :as http]
            [dealer-api.sql.drugs :as gen-sql]
            [clojure.spec.alpha :as s]))

(defn ask-for-valid []
  (assoc (http/json-response {:msg "Please send a valid drug."})
         :status 400))

(defn create-drug [{:keys [json-params]}]
  (let [new-drug (select-keys json-params [:name :availability :price])]
    (if (s/valid? ::drug new-drug)
      (http/json-response {:msg "Drug created sucessfully."
                           :id (gen-sql/insert-drug db new-drug)
                           :status 201})
      ask-for-valid)))

(defn all-drugs [_]
  (http/json-response (gen-sql/drugs db)))

(defn delete-drug [{{:keys [id]} :json-params}]
  (gen-sql/delete-drug db {:id id}))



(defn update-drug [{:keys [json-params]}]
  (let [new-drug (select-keys json-params [:id :name :availability :price])]
    (if (s/valid? ::drug new-drug)
     (http/json-response {:msg "Drug updated sucessfully."
                          :id (gen-sql/update-drug db new-drug)
                          :status 200})
     (ask-for-valid))))



(s/def ::name string?)
(s/def ::availability int?)
(s/def ::price (s/or :price int?
                     :price float?))
(s/def ::drug (s/keys :req-un [::name ::availability ::price]))

(s/valid? ::drug {:name "ho" :availability \5 :price 5.5})

(s/describe ::drug)
