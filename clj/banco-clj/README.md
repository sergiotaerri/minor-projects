# banco-clj

## About

The project essentially is a 4-resource rest api, with documented endpoints through OpenAPI Swagger, it's architecture is derived from luminus frameworks', utilizing reitit for routing, malli for schemas and hugsql for sql generation to a Postgresql database.

In it's domain logic, it implements a time-based request filtering


## Prerequisites

You will need [Leiningen][1] 2.0 or above installed and a postgres server OR
You will need [docker][2]  installed.

[1]: https://github.com/technomancy/leiningen
[2]: https://github.com/docker

## Running locally for development

1. Running `make db_dev` should bring up the database.
2. Then bringing up a repl normally with `lein repl`.
3. After it's up evaluating `(start) (migrate)`  should set up both the server and database.

## Running via docker

It has a working hml deployment script powered by docker, building a .jar and exposing it to the host machine at port 3033.

    make deploy_hml

It currently also brings up a postgres container for development and connection in this bootstraped hml environment.


Then wait a few minutes so maven fetchs all dependencies, you can verify the progrss through docker logs <banco_hml_container>
The api swagger should be acessible at port http://localhost:3003/api/api-docs
