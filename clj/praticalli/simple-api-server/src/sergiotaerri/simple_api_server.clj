(ns sergiotaerri.simple-api-server
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :refer [not-found]]
            [ring.util.response :refer [response]]
            [clojure.data.json :as json]))

(def scoreboard
  {:players
   [{:id 1 :name "johnny" :times-lost 1000}
    {:id 2 :name "clayton" :times-lost 2000}
    {:id 3 :name "ednald" :times-lost 100}]})

(defn handler [req]
  {:status 200
   :header {"Content-Type" "text/html"}
   :body "Hello from Clojure"})

(defn statistics [req]
  (println "Calling scoreboard handler...")
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str scoreboard)})

(defn player-score [{{:keys [name]} :route-params}]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (filter #(= "ednald" (:name %)) (:players scoreboard)))})

(defroutes webapp
  (context "/data" []
           (GET "/" [] statistics)
           (GET "/score/:name" [name] player-score)))

(def ^:private server-api (atom nil))

(defn create-server
  "A ring-based server listening to all http requests
  port is an Integer greater than 128"
  [routes ip port]
  (reset! server-api (server/run-server routes {:ip ip :port port})))

(defn stop-server []
  (when-not (nil? @server-api)
    (@server-api :timeout 100)
    (reset! server-api nil)))

(defn -main [& {:keys [ip port]
                :or {ip "localhost" port 8080}}]
  (println "Starting server on ip http://" ip "and port " port ".")
  (create-server #'webapp ip port))

(comment
  (do
    (require '[dealer-api.config :refer [db]])
    (require '[dealer-api.sql.drugs :as sd] :reload)))
