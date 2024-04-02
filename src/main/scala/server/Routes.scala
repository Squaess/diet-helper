package server

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import services.RedisOperations
import org.http4s.circe.CirceEntityDecoder._

object Routes {

  lazy val redisOperation = RedisOperations.Impl

  def routes: HttpRoutes[IO] =
    val dsl = Http4sDsl[IO]
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "product" =>
        for {
          keys <- redisOperation.list("product:*")
          res <- Ok(keys.asJson.noSpaces)
        } yield res
      case GET -> Root / "product" / name =>
        for {
          product <- redisOperation.get[domain.Product](domain.Product(name).id)
          res <- Ok(product.get.asJson.noSpaces)
        } yield res
      case DELETE -> Root / "product" / name =>
        redisOperation.delete(domain.Product(name).id) >> Ok()
      case req @ POST -> Root / "product" =>
        for {
          product <- req.as[domain.Product]
          _ <- redisOperation.save(product)
          res <- Ok()
        } yield res
    }
}
