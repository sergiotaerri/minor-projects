(ns banco-clj.handlers.contas
  (:require [banco-clj.db.core :as db]))

(defn criar-cliente [{{{:keys [nome]} :body} :parameters}]
  {:status 200
   :body (db/create-record! db/*db* {:resource "clientes"
                                     :fields ["nome"]
                                     :values [nome]})})

(defn criar-conta [{{:keys [body]} :parameters}]
  (let [conta-criada (db/create-record! db/*db* {:resource "contas"
                                                 :fields ["credito" "tipo" "cliente_id"]
                                                 :values [(get body :credito)
                                                          (if-let [tipo (get body  :tipo)] tipo 1)
                                                          (get body :cliente_id)]})]
    {:status 200
     :body conta-criada}))

(defn verificar-saldo [{{{:keys [id]} :query} :parameters}]
  {:status 200
   :body {:saldo (:credito (db/get-record db/*db* {:resource "contas"
                                                   :key-name "id"
                                                   :key id}))}})

(defn sacar [{{:keys [query body]} :parameters}]
  (let [id (:id query)
        quantia (:quantia body)
        conta (db/get-record db/*db* {:resource "contas"
                                      :key-name "id"
                                      :key id})
        conta-atualizada (db/update-record! db/*db* {:resource "contas"
                                                     :key-name "id"
                                                     :key id
                                                     :fields ["credito"]
                                                     :values [(- (:credito conta) quantia)]})]
    {:status 200
     :body {:qnt-atualizacoes conta-atualizada}}))

(defn depositar [{{:keys [query body]} :parameters}]
  (let [id (:id query)
        quantia (:quantia body)
        conta (db/get-record db/*db* {:resource "contas"
                                      :key-name "id"
                                      :key id})
        conta-atualizada (db/update-record! db/*db* {:resource "contas"
                                                     :key-name "id"
                                                     :key (:id conta)
                                                     :fields ["credito"]
                                                     :values [(+ (:credito conta) quantia)]})]
    {:status 200
     :body {:qnt-atualizacoes conta-atualizada}}))

(comment
  (depositar {:parameters {:query {:id 1} :body {:quantia 200.30}}})

  (sacar {:parameters {:query {:id 1} :body {:quantia 2000}}})

  (criar-cliente {:parameters {:body {:nome "Jorge Joel Joesley"}}})

  (criar-conta {:parameters {:body {:credito 200.30 :cliente_id 1}}})

  (db/get-all-records db/*db* {:resource "contas"})

  (db/update-record!)

  ;;
  )
