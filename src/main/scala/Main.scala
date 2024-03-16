import cats.effect.*
import cats.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.comcast.ip4s._
import org.http4s.ember.server._

import services.RedisOperations
import server.Routes

object Main extends IOApp.Simple:

  lazy val redisOperation = RedisOperations.Impl
  // Impure But What 90% of Folks I know do with log4s
  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  val server = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(Routes.routes.orNotFound)
    .build

  override def run: IO[Unit] =
    server.use(_ => IO.never)
