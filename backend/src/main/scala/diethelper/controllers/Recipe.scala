package diethelper.controllers

import cats.effect.IO
import cats.implicits.*
import diethelper.common.model.Recipe
import diethelper.services.RedisOperations
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl
import diethelper.services.RecipeService

object Recipe {

  val dsl = Http4sDsl[IO]
  import dsl._

  def getRoutes(recipeService: RecipeService[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        recipes <- recipeService.listRecipes
        res <- Ok(recipes.asJson.noSpaces)
      } yield res

    case GET -> Root / name =>
      for {
        recipe <- recipeService.getRecipe(name)
        res <- recipe.fold(NotFound())(r => Ok(r.asJson.noSpaces))
      } yield res

    case DELETE -> Root / name =>
      recipeService.deleteRecipe(name) >> Ok()

    case req @ POST -> Root =>
      for {
        decoded <- req.attemptAs[Recipe].value
        res <- decoded match {
          case Left(value) => BadRequest(s"Invalid recipe provided, fix the format!")
          case Right(value) => for {
            _ <- recipeService.saveRecipe(value)
            res <- Created()
          } yield res
        }
      } yield res
  }
}
