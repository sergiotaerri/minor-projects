(ns dealer-api.config)

(def db
  {:class-name "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/drugdb"
   :user "serjo"
   :password ""})
