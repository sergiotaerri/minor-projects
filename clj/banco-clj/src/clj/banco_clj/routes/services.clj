(ns banco-clj.routes.services
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.spec :as spec-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [banco-clj.middleware.formats :as formats]
   [ring.util.http-response :refer :all]
   [clojure.java.io :as io]
   [banco-clj.spec]
   [banco-clj.handlers.contas :refer [criar-conta criar-cliente
                                      verificar-saldo
                                      depositar sacar]]))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "Banco MínimoPROGWeb: "
                         :description "Feito para atividade da matéria de Programação Web"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs*"
     {:get (swagger-ui/create-swagger-ui-handler
            {:url "/api/swagger.json"
             :config {:validator-url nil}})}]]

   ["/clientes"
    {:swagger {:tags ["clientes"]}}

    [""
     {:post {:summary "Criar um cliente"
             :parameters {:body {:nome :banco-clj.spec/nome}}
             :responses {200 {:body :banco-clj.spec/cliente}}
             :handler criar-cliente}}]]

   ["/contas"
    {:swagger {:tags ["contas"]}}

    [""
     {:post {:summary "Criar uma conta"
             :parameters {:body {:credito :banco-clj.spec/credito
                                 :cliente_id :banco-clj.spec/cliente_id}}
             :responses {200 {:body :banco-clj.spec/conta}}
             :handler criar-conta}}]
    ["/{id}/saldo"
     {:put {:summary "Depositar quantia à conta"
            :parameters {:query {:id :banco-clj.spec/id}
                         :body {:quantia number?}}
            :responses {200 {:body {:saldo number?}}}
            :handler verificar-saldo}}]
    ["/{id}/depositar"
     {:put {:summary "Depositar quantia à conta"
            :parameters {:query {:id :banco-clj.spec/id}
                         :body {:quantia number?}}
            :responses {200 {:body {:qnt-atualizacoes int?}}}
            :handler depositar}}]
    ["/{id}/sacar"
     {:put {:summary "Sacar quantia da conta"
            :parameters {:query {:id :banco-clj.spec/id}
                         :body {:quantia number?}}
            :responses {200 {:body {:qnt-atualizacoes int?}}}
            :handler sacar}}]]])
