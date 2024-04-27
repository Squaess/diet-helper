package domain

import scala.reflect.ClassTag

final case class Recipe(
    name: String,
    products: Vector[RecipeProduct],
    calories: Double,
    description: String
) extends RedisDocument {

  override def table: String = Recipe.table

  override def id: String = Recipe.id(name)

}

object Recipe {
  val table = "recipe"

  def id(name: String) = s"$table:$name"

  def extractProducts[A <: ListCategory: ClassTag](
      recipe: Recipe
  ): Vector[RecipeProduct] =
    recipe.products.filter(recProduct =>
      recProduct.product.category match
        case _: A => true
        case _    => false
    )
}

final case class RecipeDSL(
    name: String,
    products: Vector[RecipeProductDSL],
    calories: Double,
    description: String
)
