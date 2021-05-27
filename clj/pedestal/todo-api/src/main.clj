(ns main
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test]))

(defonce database (atom {}))

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok (partial response 200))
(def created (partial response 201))
(def accepted (partial response 202))

;;Handlers
(defn make-list [nm]
  {:name nm
   :items {}})

(defn make-list-item [nm]
  {:name nm
   :done? false})

(defn find-list-by-id [dbval db-id]
  (get dbval db-id))

(defn find-list-item-by-ids [dbval list-id item-id]
  (get-in dbval [list-id :items item-id] nil))

(defn list-item-add
  [dbval list-id item-id new-item]
  (if (contains? dbval list-id)
    (assoc-in dbval [list-id :items item-id] new-item)
    dbval))



;;Interceptors


(def echo
  {:name :echo
   :enter
   (fn [context]
     (let [request (:request context)
           response (ok context)]
       (assoc context :response response)))})

(def db-interceptor
  {:name ::db-interceptor
   :enter
   (fn [context]
     (update context :request assoc :database @database))
   :leave
   (fn [context]
     (if-let [[op & args] (:tx-data context)]
       (do
         (apply swap! database op args);;update database
         (assoc-in context [:request :database] @database))
       context))})

(def list-create
  {:name ::list-create
   :enter
   (fn [context]
     (let [nm (get-in context [:request :query-params :name] "Unamed Item")
           new-list (make-list nm)
           db-id (str (gensym "1"))
           url (route/url-for :list-view :params {:list-id db-id})]
       (assoc context
              :response (created new-list "Location" url)
              :tx-data [assoc db-id new-list])))})

(def list-view
  {:name ::list-create
   :enter
   (fn [context]
     (if-let [db-id (get-in context [:request :query-params :list-id])]
       (if-let
           [the-list (find-list-by-id (get-in context [:request :database])
                                      db-id)]
         (assoc context :result the-list)
         context)
       context)
     )})

(def entity-render
  {:name ::entity-render
   :leave
   (fn [context]
     (if-let [item (:result context)]
       (assoc context :response (ok item))
       context))})

(def list-item-view
  {:name ::list-item-view
   :leave
   (fn [context]
     (if-let [list-id (get-in context [:request :path-params :list-id])]
       (if-let [item-id (get-in context [:request :path-params :item-id])]
         (if-let [item (find-list-item-by-ids (get-in context [:request :database]) list-id item-id)]
           (assoc context :result item)
           context)
         context)
       context)
     )})

(def list-item-create
  {:name :list-item-create
   :enter
   (fn [context]
     (if-let [list-id (get-in context [:request :path-params :list-id])]
       (let [nm       (get-in context [:request :query-params :name] "Unnamed Item")
             new-item (make-list-item nm)
             item-id  (str (gensym "i"))]
         (-> context
             (assoc :tx-data  [list-item-add list-id item-id new-item])
             (assoc-in [:request :path-params :item-id] item-id)))
       context))})

(def routes
  (route/expand-routes
   #{["/todo"                    :post   [db-interceptor list-create]]
     ["/todo"                    :get    echo :route-name :list-query-form];;no need to help out pedestal with a route-name anymore since he'll use the name of the last inteceptor
     ["/todo/:list-id"           :get    [entity-render db-interceptor list-view]]
     ["/todo/:list-id"           :post   [entity-render list-item-view db-interceptor list-item-create]
     ["/todo/:list-id/:item-id"  :get    [entity-render list-item-view db-interceptor] :route-name ::item-detail]
     ["/todo/:list-id/:item-id"  :put    echo :route-name :list-item-update]
     ["/todo/:list-id/:item-id"  :delete echo :route-name :list-item-delete]}))



;; Interactive development support


(def service-map {::http/routes routes
                  ::http/type :jetty
                  ::http/port 8890})

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/server (http/create-server
                        (assoc service-map
                               ::http/join? false)))))
(defn stop-dev []
  (http/stop @server))

(start-dev)
(start-dev)
(defn restart []
  (stop-dev)
  (start-dev))

;;Helpers

(defn test-request [verb url]
  (io.pedestal.test/response-for (::http/service-fn @server) verb url))
