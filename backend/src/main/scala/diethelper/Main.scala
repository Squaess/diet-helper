package diethelper

import cats.effect._
import com.comcast.ip4s._
import diethelper.controllers.Diet
import diethelper.controllers.Product
import diethelper.controllers.Recipe
import diethelper.services.RedisOperations
import diethelper.services.RedisOperationsImpl
import org.http4s.ember.server._
import org.http4s.server.Router
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp {

  implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]
  lazy val redisResource = RedisClientSetup.createRedisResource[IO]

  def httpApp(redisOps: RedisOperations[IO]) =
    Router(
      "/product" -> Product.getRoutes(redisOps),
      "/recipe" -> Recipe.getRoutes(redisOps),
      "/diet" -> Diet.getRoutes(redisOps)
    ).orNotFound

  def server(config: AppConfig) = for {
    redis <- redisResource(config.redis)
    redisOps = new RedisOperationsImpl(redis)
    server <- EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp(redisOps))
      .build
  } yield server

  def run(args: List[String]): IO[ExitCode] =
    AppConfig.load.flatMap { conf =>
      server(conf).use(_ => IO.never)
    }
}
