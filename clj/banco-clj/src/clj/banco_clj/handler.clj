(ns banco-clj.handler
  (:require
    [banco-clj.middleware :as middleware]
    [banco-clj.routes.services :refer [service-routes]]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring :as ring]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [banco-clj.env :refer [defaults]]
    [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
      [["/" {:get
             {:handler (constantly {:status 200 :body ""})}}]
       (service-routes)])
    (ring/routes
      (ring/create-resource-handler
        {:path "/"})
      (ring/create-default-handler))))

(defn app []
  (middleware/wrap-base #'app-routes))
