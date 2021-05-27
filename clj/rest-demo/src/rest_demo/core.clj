(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]))


(defn hello-page [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (-> (pp/pprint req)
             (str "Hello " (:name (:params req))))})

(defn simple-body-page [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(defn request-example [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (->>
          (pp/pprint req)
          (str "Request Object: " req))})

(def people-collection (atom []))

(defn add-person [firstname surname]
  (swap! people-collection conj {:firstname (str/capitalize firstname) :surname (str/capitalize surname)}))

(defn people-handler [req]
  {:status 200
   :header {"Content-Type" "text/json"}
   :body {json/write-str @people-collection}})

(defn add-people-handler [{{:keys [name surname]} :params}]
  {:status 200
   :header {"Content-Type" "text/json"}
   :body (str (json/write-str (add-person name surname)))})


(people-handler {:params {:name "jose" :surname "esoj"}})

(defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/hello" [] hello-page)
  (GET "/request" [] request-example)
  (GET "/people" [] people-handler)
  (GET "/people/add" [] add-people-handler)
  (route/not-found "Error, page not found!"))

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
                                        ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
                                        ; Run the server without ring defaults
                                        ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))

