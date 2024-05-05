package server

import cats.effect.IO
import cats.syntax.all._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import services.RedisOperations
import org.http4s.circe.CirceEntityDecoder._
import domain.Recipe
import services.Recipes
import domain.ListCategory
import domain.RecipeDSL

object Routes {

  val dsl = Http4sDsl[IO]
  import dsl._

  lazy val redisOperation = RedisOperations.Impl

  def diet: HttpRoutes[IO] = HttpRoutes.of[IO] { case req @ POST -> Root =>
    for {
      diet <- req.as[Vector[(String, Double)]]
      recipes <- Recipes.getRecipes(diet.map(_._1))
      x = diet.zip(recipes).map { case ((_, factor), r) =>
        Recipes.multiplyRecipe(r, factor)
      }
      shoppingList = Recipes.createShoppingList(x)
      res <- Ok(shoppingList.asJson.noSpaces)
    } yield res

  }

  def recipe: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root =>
        for {
          keys <- redisOperation.list("recipe:*")
          res <- Ok(keys.asJson.noSpaces)
        } yield res

      case GET -> Root / name =>
        for {
          recipe <- redisOperation.get[Recipe](Recipe.id(name))
          res <- recipe.fold(NotFound())(r => Ok(r.asJson.noSpaces))
        } yield res

      case DELETE -> Root / name =>
        redisOperation.delete(Recipe.id(name)) >> Ok()

      case req @ POST -> Root =>
        for {
          recipe <- req.as[RecipeDSL]
          result <- Recipes.saveRecipe(recipe).option
          res <- result match
            case None => BadRequest()
            case Some(value) => Ok()
          
        } yield res
    }

  def product: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root =>
        for {
          keys <- redisOperation.list(s"${domain.Product.table}:*")
          res <- Ok(keys.asJson.noSpaces)
        } yield res

      case GET -> Root / name =>
        for {
          product <- redisOperation.get[domain.Product](domain.Product.id(name))
          res <- product.fold(NotFound())(x => Ok(x.asJson.noSpaces))
        } yield res

      case DELETE -> Root / name =>
        redisOperation.delete(domain.Product.id(name)) >> Ok()

      case req @ POST -> Root =>
        for {
          product <- req
            .as[domain.Product]
            .onError(a => IO.println(a.toString()))
          _ <- redisOperation.save(product)
          res <- Ok()
        } yield res
    }
}
