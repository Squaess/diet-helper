package diethelper.services

import cats.effect.IO
import cats.implicits._
import diethelper.domain.db.Recipe
import diethelper.domain.db.ShoppingList
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityDecoder._
import diethelper.domain.db.RecipeProduct
import diethelper.domain.db.Fridge
import diethelper.domain.db.Others
import diethelper.domain.db.Vegetables
import diethelper.domain.controller.RecipeDSL
import diethelper.domain.db
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.syntax._
import cats.effect.kernel.Sync
import diethelper.domain.db.Product

object Recipes {

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  val redisOperation = RedisOperations.Impl

  def saveRecipe(recipeDSL: RecipeDSL) = for {
    recipe <- checkProducts(recipeDSL)
    _ <- redisOperation.save[Recipe](recipe)
  } yield ()

  def getRecipes(ids: Vector[String]): IO[Vector[Recipe]] = ids
    .map { recipe =>
      redisOperation.get[Recipe](Recipe.id(recipe))
    }
    .sequence
    .map { maybeRecipe =>
      if (maybeRecipe.exists(_.isEmpty)) {
        Vector.empty[Recipe]
      } else {
        maybeRecipe.map(_.get)
      }
    }

  private def checkProducts(recipe: RecipeDSL): IO[Recipe] = {
    for {
      res <- recipe.products
        .map(prod =>
          redisOperation.get[db.Product](db.Product.id(prod.name))
        )
        .sequence
      shouldFail = res.exists(_.isEmpty)
      res <-
        if (shouldFail) {
          IO.unit.flatTap(_ => error"Couldn't find some product") >>
          IO.raiseError[Recipe](
            throw new RuntimeException("TODO")
          )
        } else
          IO.pure(
            Recipe(
              recipe.name,
              res.zip(recipe.products).map { case (prod, prodDsl) =>
                RecipeProduct(prod.get, prodDsl.quantity)
              },
              recipe.calories,
              recipe.description
            )
          )
    } yield res
  }

  def multiplyRecipe(recipe: Recipe, factor: Double): Recipe =
    recipe.copy(products =
      recipe.products.map(p => p.copy(p.product, p.quantity * factor))
    )

  def createShoppingList(recipes: Vector[Recipe]): ShoppingList = {
    val fridgeProducts =
      reduceProductsInCategory(
        recipes.flatMap(Recipe.extractProducts[Fridge.type])
      )
    val othersProducts = reduceProductsInCategory(
      recipes.flatMap(Recipe.extractProducts[Others.type])
    )
    val vegetableProducts =
      reduceProductsInCategory(
        recipes.flatMap(Recipe.extractProducts[Vegetables.type])
      )
    ShoppingList(othersProducts, fridgeProducts, vegetableProducts)
  }

  def reduceProductsInCategory(
      products: Vector[RecipeProduct]
  ): Vector[RecipeProduct] = {
    products.groupBy(_.product.id).mapValues(_.reduce(_ + _)).values.toVector
  }
}
