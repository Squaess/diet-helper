package diethelper.services

import cats.effect.IO
import cats.implicits._
import diethelper.domain.db.DbRecipe
import diethelper.common.model.Recipe
import diethelper.domain.db.ShoppingList
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityDecoder._
import diethelper.domain.db.RecipeProduct
import diethelper.domain.db.Fridge
import diethelper.domain.db.Others
import diethelper.domain.db.Vegetables
import diethelper.domain.db
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.syntax._
import cats.effect.kernel.Sync
import diethelper.domain.db.Product

object Recipes {

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  val redisOperation = RedisOperations.Impl

  def saveRecipe(recipeDSL: Recipe) = for {
    recipe <- checkProducts(recipeDSL)
    _ <- redisOperation.save[DbRecipe](recipe)
  } yield ()

  def getRecipes(ids: Vector[String]): IO[Vector[DbRecipe]] = ids
    .map { recipe =>
      redisOperation.get[DbRecipe](DbRecipe.id(recipe))
    }
    .sequence
    .map { maybeRecipe =>
      if (maybeRecipe.exists(_.isEmpty)) {
        Vector.empty[DbRecipe]
      } else {
        maybeRecipe.map(_.get)
      }
    }

  private def checkProducts(recipe: Recipe): IO[DbRecipe] = {
    for {
      res <- recipe.products
        .map(prod =>
          redisOperation.get[db.Product](db.Product.id(prod._1.name))
        )
        .sequence
      shouldFail = res.exists(_.isEmpty)
      res <-
        if (shouldFail) {
          IO.unit.flatTap(_ => error"Couldn't find some product") >>
          IO.raiseError[DbRecipe](
            throw new RuntimeException("TODO")
          )
        } else
          IO.pure(
            DbRecipe(
              recipe.name,
              res.zip(recipe.products).map { case (prod, prodDsl) =>
                RecipeProduct(prod.get, prodDsl._2)
              }.toVector,
              recipe.calories,
              recipe.steps
            )
          )
    } yield res
  }

  def multiplyRecipe(recipe: DbRecipe, factor: Double): DbRecipe =
    recipe.copy(products =
      recipe.products.map(p => p.copy(p.product, p.quantity * factor))
    )

  def createShoppingList(recipes: Vector[DbRecipe]): ShoppingList = {
    val fridgeProducts =
      reduceProductsInCategory(
        recipes.flatMap(DbRecipe.extractProducts[Fridge.type])
      )
    val othersProducts = reduceProductsInCategory(
      recipes.flatMap(DbRecipe.extractProducts[Others.type])
    )
    val vegetableProducts =
      reduceProductsInCategory(
        recipes.flatMap(DbRecipe.extractProducts[Vegetables.type])
      )
    ShoppingList(othersProducts, fridgeProducts, vegetableProducts)
  }

  def reduceProductsInCategory(
      products: Vector[RecipeProduct]
  ): Vector[RecipeProduct] = {
    products.groupBy(_.product.id).mapValues(_.reduce(_ + _)).values.toVector
  }
}
