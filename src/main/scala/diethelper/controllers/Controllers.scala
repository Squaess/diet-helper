package diethelper.controllers

import cats.effect.IO
import diethelper.domain.db.Recipe
import diethelper.domain.controller.RecipeDSL
import diethelper.services.Recipes
import diethelper.services.RedisOperations
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.headers._
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl
import diethelper.views.HomePage
import scalatags.Text
import org.http4s.MediaType
import diethelper.views.RecipeView.newRecipeForm
import diethelper.views.RecipeView.productField
import diethelper.views.ProductView

object Controllers {

  val dsl = Http4sDsl[IO]
  import dsl._

  lazy val redisOperation = RedisOperations.Impl

  def homePage: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      scalatagsToResponse(HomePage.generate())

    case GET -> Root / "clicked"       => Ok("This is a content")
    case GET -> Root / "trigger_delay" => Ok("I don't know")
  }

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
          // res <- scalatagsToResponse(
          //   HomePage.generate(HomePage.listView(keys, 12))
          // )
          res <- Ok(keys.asJson.noSpaces)
        } yield res

      case GET -> Root / "new" =>
        scalatagsToResponse(HomePage.generate(newRecipeForm()))

      case GET -> Root / "new" / "addProductField" / currentProductCountString => {
        val currentProductCount = currentProductCountString.toInt
        val newProductFieldHtml = productField(currentProductCount)
        scalatagsToResponse(newProductFieldHtml)
        // Ok(newProductFieldHtml).as("text/html")
      }

      case GET -> Root / name =>
        for {
          recipe <- redisOperation.get[Recipe](Recipe.id(name))
          res <- recipe.fold(NotFound())(r => Ok(r.asJson.noSpaces))
        } yield res

      case DELETE -> Root / name =>
        redisOperation.delete(Recipe.id(name)) >> Ok()

      case req @ POST -> Root / "new" =>
        for {
          recipe <- req.as[RecipeDSL]
          result <- Recipes.saveRecipe(recipe).option
          res <- result match
            case None        => BadRequest()
            case Some(value) => Ok()

        } yield res
    }

  def products: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root =>
    for {
      keys <- redisOperation.list(
        s"${diethelper.domain.db.Product.table}:*"
      )
      res <- Ok(keys.asJson.noSpaces)
    } yield res
  }

  def product: HttpRoutes[IO] =
    HttpRoutes.of[IO] {

      case GET -> Root / "new" => scalatagsToResponse(ProductView.newProductRow)

      case GET -> Root / "empty" =>
        Ok(ProductView.empty).map(
          _.withContentType(`Content-Type`(MediaType.text.html))
        )

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

  private def scalatagsToResponse(view: Text.TypedTag[String]) = Ok(
    body = view.render
  ).map(_.withContentType(`Content-Type`(MediaType.text.html)))

}
