package diethelper.controllers

import cats.effect.IO
import cats.implicits.*
import diethelper.domain.db.DbRecipe
import diethelper.common.model.Recipe
import diethelper.services.Recipes
import diethelper.services.RedisOperations
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl

object Controllers {

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
          recipe <- redisOperation.get[DbRecipe](DbRecipe.id(name))
          res <- recipe.fold(NotFound())(r => Ok(r.asJson.noSpaces))
        } yield res

      case DELETE -> Root / name =>
        redisOperation.delete(DbRecipe.id(name)) >> Ok()

      case req @ POST -> Root =>
        for {
          recipe <- req.as[Recipe]
          result <- Recipes.saveRecipe(recipe).option
          res <- result match
            case None        => BadRequest()
            case Some(value) => Ok()

        } yield res
    }

  def product: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root => for {
        keys <- redisOperation.list(s"${diethelper.domain.db.Product.table}:*")
        products <- keys.map( name => redisOperation.get[diethelper.domain.db.Product](diethelper.domain.db.Product.id(name))).sequence
        res <- Ok(products.asJson.noSpaces)
      } yield res

      case GET -> Root / name =>
        for {
          product <- redisOperation.get[diethelper.domain.db.Product](
            diethelper.domain.db.Product.id(name)
          )
          res <- product.fold(NotFound())(x => Ok(x.asJson.noSpaces))
        } yield res

      case DELETE -> Root / name =>
        redisOperation.delete(diethelper.domain.db.Product.id(name)) >> Ok()

      case req @ POST -> Root =>
        for {
          product <- req
            .as[diethelper.domain.db.Product]
            .onError(a => IO.println(a.toString()))
          _ <- redisOperation.save(product)
          res <- Ok()
        } yield res
    }
}
