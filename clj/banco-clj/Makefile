db_dev:
	docker-compose up -d --remove-orphans banco-clj-db-test banco-clj-db-dev
deploy_dev:
	docker-compose up -d --remove-orphans banco-clj-dev banco-clj-db-dev
	docker-compose up -d  banco-clj  # dificuldade para conectar via repl q
	DATABASE_URL="postgres://localhost:5433/bancos_api?user=root&password=postgres" lein repl &
deploy_hml:
	docker-compose up -d --remove-orphans banco-clj-db-dev banco-clj-hml 
