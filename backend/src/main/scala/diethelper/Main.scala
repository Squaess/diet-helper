package diethelper

import cats.effect.*
import com.comcast.ip4s._
import org.http4s.ember.server._
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.loggerFactoryforSync
import diethelper.controllers.Controllers

object Main extends IOApp.Simple:

  val httpApp =
    Router(
      "/product" -> Controllers.product,
      "/recipe" -> Controllers.recipe,
      "/diet" -> Controllers.diet
    ).orNotFound

  val server = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(httpApp)
    .build

  override def run: IO[Unit] =
    server.use(_ => IO.never)
