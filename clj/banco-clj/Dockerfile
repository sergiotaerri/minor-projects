ARG http_proxy
ARG https_proxy

FROM clojure:lein-2.9.5 as builder
ENV LEIN_JVM_OPTS="-Dhttps.protocols=TLSv1.2"
WORKDIR /app
COPY project.clj /app
RUN lein deps #cache dependencies
COPY . /app
RUN mv -v "$(lein uberjar | sed -n 's/^Created \(.*\.jar\)/\1/p' | head -2 | tail -1)" \
    /app/target/uberjar/app-standalone.jar

### 

FROM openjdk:8-alpine
COPY --from=builder /app/target/uberjar/app-standalone.jar /app/enderecos-api/app-standalone.jar
CMD ["java", "-jar", "/app/enderecos-api/app-standalone.jar"]
