package server

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import services.RedisOperations

object Routes {

  lazy val redisOperation = RedisOperations.Impl

  def routes: HttpRoutes[IO] =
    val dsl = Http4sDsl[IO]
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "get" / name =>
        for {
          product <- redisOperation.get[domain.Product](s"product:$name")
          res <- Ok(product.get.asJson.noSpaces)
        } yield res
      case GET -> Root / "del" / name =>
        redisOperation.deleteById(name) >> Ok()
      case POST -> Root / "product" / name =>
        redisOperation.save(domain.Product(name)) >> Ok()
    }
}
