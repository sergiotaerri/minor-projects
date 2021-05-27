(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clojure.string :as s]
            [io.pedestal.http.content-negotiation :as conneg]
            [clojure.data.json :as json]))

(def unmentionable #{"YHWH" "Voldemort" "Mxyzptlk" "Rumplestiltskin"})

(def supported-types ["text/html" "applications/edn" "application/json" "text/plain"])

(def con-neg-init (conneg/negotiate-content supported-types))

(defn ok [body]
  {:status 200 :body body
   ;; The servlet interceptor does not infer text/html type (at leas the example one), thus the need for declaring it here
  ;; :headers {"Content-Type" "text/html"}
   })

(defn bad-request []
  {:status 400 :body "400 Bad request"})

(defn not-found []
  {:status 404 :body "404 Not Found"})

(defn greeting-for [greet-name]
  (cond (unmentionable greet-name) bad-request
        (empty? greet-name)                    "Hello World!"
        :else                                  (str "Hello, " greet-name "!")))

;; Handlers
(defn respond-hello [request]
  (let [greet-name (get-in request [:query-params :name])
        response   (greeting-for greet-name)]
    (ok response)))

;; Interceptors
(def echo
  {:name ::echo
   :enter #(assoc % :response (ok (:request %)))})


;; (def coerce-body
;;   {:name ::coerce-body
;;    :leave
;;    (fn [context]
;;      ;; The :accept :failed actually get the accept OR failed values (negotiator interceptor wouldn't fill both keys), clever stuff
;;      (let [accepted     (get-in context [:request :accept :failed] "text/plain")
;;            response     (get context :response)
;;            body         (get response :body)
;;            coerced-body (case accepted
;;                           "text/html"        body
;;                           "text/plain"       body
;;                           "application/edn"  (pr-str body)
;;                           "apnlication/json" (json/write-str body))
;;            updated-response (assoc response
;;                                    :headers {"Content-type" accepted}
;;                                    :body coerced-body)]
;;        (assoc context :response updated-response)
;;        ))})


(defn accepted-type [context]
  (get-in context [:request :accept :field] "text/plain"))

(defn transform-content [body type]
  (case type
    "text/html"        body
    "text/plain"       body
    "application/edn"  (pr-str body)
    "application/json" (json/write-str body)))

(defn coerce-to
  [response content-type]
  (-> response
      (update :body transform-content content-type)
      (assoc-in [:headers "Content-Type"] content-type)))


(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [context]
     (cond-> context
       (nil? (get-in context [:response :headers "Content-Type"]))
       ;; (if (get-in context [:response :headers "Content-Type"])
       ;;   context
       ;;   (update-in context [:reponse] coerce-to (accepted-type context)))
       (update-in [:response] coerce-to (accepted-type context))))})

(def routes
  (route/expand-routes
   #{["/greet" :get [coerce-body con-neg-init respond-hello] :route-name :greet]
     ["/echo" :get echo :route-name :echo]}))

(defn create-server []
  (http/create-server
   {::http/routes routes
    ::http/type   :jetty
    ::http/port   8890}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map
                              ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))
