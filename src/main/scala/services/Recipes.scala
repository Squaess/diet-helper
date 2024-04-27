package services

import cats.effect.IO
import cats.implicits._
import domain.Recipe
import domain.ShoppingList
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityDecoder._
import domain.RecipeProduct
import domain.Fridge
import domain.Others
import domain.Vegetables
import domain.RecipeDSL

object Recipes {

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
          redisOperation.get[domain.Product](domain.Product.id(prod.name))
        )
        .sequence
      shouldFail = res.exists(_.isEmpty)
    } yield
      if (shouldFail) throw new RuntimeException("blah")
      else
        Recipe(
          recipe.name,
          res.zip(recipe.products).map { case (prod, prodDsl) =>
            RecipeProduct(prod.get, prodDsl.quantity)
          },
          recipe.calories,
          recipe.description
        )
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
