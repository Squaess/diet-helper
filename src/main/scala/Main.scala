import cats.effect.*
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
// import org.http4s.implicits._
import org.http4s.ember.server._

import services.RedisOperations

object Main extends IOApp.Simple:

  lazy val redisOperation = RedisOperations.Impl

  val helloWorldService = HttpRoutes
    .of[IO] {
      case GET -> Root / "hello" / name =>
        redisOperation.setById(name, "1") >> Ok()
      case GET -> Root / "get" / name =>
        redisOperation.getById(name).flatMap(x => Ok(x.get))
    }
    .orNotFound

  val server = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(helloWorldService)
    .build

  override def run: IO[Unit] =
    server.use(_ => IO.never)
