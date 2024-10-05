package diethelper.controllers

import cats.effect.IO
import diethelper.common.model.Recipe
import diethelper.services.RedisOperations
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl

object Recipe {

  val dsl = Http4sDsl[IO]
  import dsl._

  def getRoutes(redisOperations: RedisOperations[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        keys <- redisOperations.list[Recipe]
        res <- Ok(keys.asJson.noSpaces)
      } yield res

    case GET -> Root / name =>
      for {
        recipe <- redisOperations.get[Recipe](name)
        res <- recipe.fold(NotFound())(r => Ok(r.asJson.noSpaces))
      } yield res

    case DELETE -> Root / name =>
      redisOperations.delete[Recipe](name) >> Ok()

    case req @ POST -> Root =>
      for {
        recipe <- req.as[Recipe]
        _ <- redisOperations.save[Recipe](recipe)
        res <- Created()
      } yield res
  }
}
