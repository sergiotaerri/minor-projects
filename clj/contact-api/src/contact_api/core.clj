(ns contact-api.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :refer [not-found]]
            [ring.util.response :refer [response]]
            [clojure.data.json :as json]
            [contact-api.config :as config]
            [toucan.db :as db]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(def pessoa-table-ddl
  (jdbc/create-table-ddl :pessoa
                         [[:id :int :primary :key]
                          [:nome "varchar(60)"]
                          [:sobrenome "varchar(100)"]]
                         {:table-spec "ENGINE=InnoDB"}))

(def contato-pessoa-table-dll
  (jdbc/create-table-ddl :tipo-contato
                         [[:id :int :primary :key]
                          [:pessoa-id :int :foreign :key]
                          [:tipo-pessoa-id :int :foreign :key]
                          [:nome "varchar(60)"]
                          [:sobrenome "varchar(100)"]]
                         {:table-spec "ENGINE=InnoDB"}))

(def contato-tipo-table-ddl
  (jdbc/create-table-ddl :tipo-contato
                         [[:id :int :primary :key]
                          [:nome "varchar(60)"]
                          [:sobrenome "varchar(100)"]]
                         {:table-spec "ENGINE=InnoDB"}))

(jdbc/query [config/db-config "CREATE TABLE agenda.pessoa (
    id int NOT NULL,
    nome varchar(50) NOT NULL,
    sobrenome varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE agenda.tipo_contato (
    id int NOT NULL,
    nome varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE agenda.contato_pessoa (
    id int NOT NULL,
    contato varchar(50) NOT NULL,
    pessoa_id int NOT NULL,
    tipo_contato_id int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (pessoa_id) REFERENCES pessoa(id),
    FOREIGN KEY (tipo_contato_id) REFERENCES tipo_contato(id)
);

INSERT INTO agenda.tipo_contato (id, nome)
VALUES
       (1, 'Telefone'),
       (2, 'Celular'),
       (3, 'E-mail'),
       (4, 'Outros')"])





(def ^:private server-api (atom nil))

(def scoreboard
  {:players
   [{:id 1 :name "johnny" :times-lost 1000}
    {:id 2 :name "clayton" :times-lost 2000}
    {:id 3 :name "ednald" :times-lost 100}]})

(defn hello-world [req]
  {:status 200
   :header {"Content-type" "application/json"}
   :body (json/write-str scoreboard)})



(defroutes webapp
  (context "/api" []
           (context "/contatos" []
                    (GET "/" [] hello-world)
                    (POST "/" [] hello-world)
                    (GET "/:id" [] hello-world)
                    (POST "/:id" [] hello-world))))



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
  (db/set-default-db-connection! config/db-config)
  (println "Starting server on ip http://" ip "and port " port ".")
  (create-server #'webapp ip port))
