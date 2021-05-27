(ns contact-api.config
  (:require [toucan.db :as db]
            ))


(def db-config
  {:dbtype "mysql"
   :dbname "contactdb"
   :user "root"
   :subname "//localhost:3306/contactdb"
   :password "1234"})


(db/set-default-db-connection! db-config)

