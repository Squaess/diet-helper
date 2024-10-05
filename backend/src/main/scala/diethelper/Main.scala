package diethelper

import cats.effect.*
import com.comcast.ip4s.*
import diethelper.controllers.Diet
import diethelper.controllers.Product
import diethelper.controllers.Recipe
import diethelper.services.RedisOperations
import diethelper.services.RedisOperationsImpl
import org.http4s.ember.server.*
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.loggerFactoryforSync

object Main extends IOApp.Simple:

  lazy val redisResource = RedisClientSetup.createRedisResource[IO]

  def httpApp(redisOps: RedisOperations[IO]) =
    Router(
      "/product" -> Product.getRoutes(redisOps),
      "/recipe" -> Recipe.getRoutes(redisOps),
      "/diet" -> Diet.getRoutes(redisOps)
    ).orNotFound

  val server = for {
    redis <- redisResource
    redisOps = new RedisOperationsImpl(redis)
    server <- EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp(redisOps))
      .build
  } yield server

  override def run: IO[Unit] =
    server.use(_ => IO.never)
