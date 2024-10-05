package diethelper.controllers

import cats.effect.IO
import cats.implicits.*
import diethelper.common.model.MyProduct
import diethelper.services.RedisOperations
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl

object Product {
  val dsl = Http4sDsl[IO]
  import dsl._

  def getRoutes(redisOperations: RedisOperations[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root => for {
      keys <- redisOperations.list[MyProduct]
      products <- keys.map(name => redisOperations.get[MyProduct](name)).sequence
      res <- Ok(products.asJson.noSpaces)
    } yield res

    case GET -> Root / name =>
      for {
        product <- redisOperations.get[MyProduct](name)
        res <- Ok(product.asJson.noSpaces)
      } yield res

    case DELETE -> Root / name =>
      redisOperations.delete[MyProduct](name) >> Ok()

    case req @ POST -> Root =>
      for {
        product <- req.as[MyProduct].onError(a => IO.println(a.toString()))
        _ <- redisOperations.save(product)
        res <- Created()
      } yield res
  }
}
