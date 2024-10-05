package diethelper.services

import cats.effect.IO
import cats.effect.Sync
import cats.implicits.*
import diethelper.common.model.ListCategory
import diethelper.common.model.Recipe
import diethelper.common.model.RecipeProduct
import diethelper.common.model.ShoppingList
import io.circe.generic.auto.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object DietService {
  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  val redisOperations = RedisOperations.Impl

  def getRecipes(ids: List[String]): IO[List[Recipe]] = ids
    .map { recipe => redisOperations.get[Recipe](recipe) }
    .sequence
    .map { maybeRecipe =>
      if (maybeRecipe.exists(_.isEmpty)) {
        List.empty[Recipe]
      } else {
        maybeRecipe.map(_.get)
      }
    }

  def multiplyRecipe(recipe: Recipe, factor: Double): Recipe =
    recipe.copy(products =
      recipe.products.map(p => p.copy(p._1, p._2 * factor))
    )

  def createShoppingList(recipes: List[Recipe]): ShoppingList = {
    val products = recipes.flatMap(_.products)
    val groupped = products.groupBy { case (p, n) => p.category }
    val x = for {
      category <- ListCategory.values
      categoryProducts <- groupped.get(category)
      reducedCategory = reduceCategoryProducts(categoryProducts)
    } yield {category -> reducedCategory}
    val shoppingMap = x.toMap

    ShoppingList(
      others = shoppingMap(ListCategory.Others),
      fridge = shoppingMap(ListCategory.Fridge),
      vegetables = shoppingMap(ListCategory.Vegetables)
    )
  }

  def reduceCategoryProducts(
      products: List[RecipeProduct]
  ): List[RecipeProduct] = {
    products
      .groupBy(_._1.name)
      .mapValues(_.reduce(myProductReducer))
      .values
      .toList
  }

  def myProductReducer(
      prod1: RecipeProduct,
      prod2: RecipeProduct
  ): RecipeProduct = {
    (prod1._1, prod1._2 + prod2._2)
  }

}
