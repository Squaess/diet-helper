# Diet helper

## Backend
Backend is running using the http4s and redis4cats.

### Running backend only
Backend service requires the redis connection. You can spawn a single resdis using `docker compose up -d redis`.
You can assmbly the backend fatJar using the `sbt backend/assembly`. The jar should be placed under `backend/target/scala-3.5.2/app.jar`. You can run it using `java -jar app.jar`.

Note: you might need to provide some env variable (see `backend/src/main/resources/application.conf`) like `REDIS_PORT=6379 REDIS_HOST=localhost`.

## Frontend
Frontend is using scalaJS with laminar.

### Run frontend

1. `cd frontend`
2. This is only for development there should be something else for prodction: `npm run dev`

## TODO

* refactor
* docker-compose not rebuild every time `docker compose build && docker compose up -d`
* unit-tests

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).
