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

object Recipes {

  val redisOperation = RedisOperations.Impl

  def getRecipe(name: String) = ???

  def saveRecipe(recipe: Recipe) = for {
    allSaved <- checkProducts(recipe)
    _ <- if (allSaved) then redisOperation.save[Recipe](recipe) else IO.unit
  } yield ()

  private def checkProducts(recipe: Recipe): IO[Boolean] = {
    for {
      res <- recipe.products
        .map(prod => redisOperation.get[domain.Product](prod.product.id))
        .sequence
    } yield res
      .map(_.isDefined)
      .map(x =>
        if !x then IO.println("Not in db")
        x
      )
      .reduce(_ && _)
  }

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
