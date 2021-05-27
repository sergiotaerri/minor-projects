(ns user
  (:require [io.pedestal.http :as http]
            [clojure.tools.namespace.repl :refer [refresh]]
            [dealer-api.core :refer [service-map]]))

(defonce server (atom nil))

(defn go []
  (reset! server (http/create-server
                  (assoc service-map
                         ::http/join? false)))
  ((::http/start-fn @server))
  (prn  (str "Server started on localhost:" (::http/port service-map))))

(defn halt []
  (http/stop @server))

(defn reset []
  (halt)
  (refresh :after 'dealer-api.core/go))
