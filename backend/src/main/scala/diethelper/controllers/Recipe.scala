package diethelper.controllers

import cats.effect.IO
import cats.implicits._
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
        recipes <- keys.map(redisOperations.get[Recipe](_)).traverse(_.map(_.toList)).map(_.flatten)
        res <- Ok(recipes.asJson.noSpaces)
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
        decoded <- req.attemptAs[Recipe].value
        res <- decoded match {
          case Left(value) => BadRequest(s"Invalid recipe provided, fix the format!")
          case Right(value) => for {
            _ <- redisOperations.save[Recipe](value)
            res <- Created()
          } yield res
        }
      } yield res
  }
}
