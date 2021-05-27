(ns sergiotaerri.simple-api-client
  (:gen-class)
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as client]))

(def scoreboard (@(client/get "https://practicalli.github.io/blog/")))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
