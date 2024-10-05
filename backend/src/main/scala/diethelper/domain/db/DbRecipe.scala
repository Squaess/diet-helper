package diethelper.domain.db

import scala.reflect.ClassTag

final case class DbRecipe(
    name: String,
    products: Vector[RecipeProduct],
    calories: Double,
    description: String
) extends RedisDocument {

  override def table: String = DbRecipe.table

  override def id: String = DbRecipe.id(name)

}

object DbRecipe {
  val table = "recipe"

  def id(name: String) = s"$table:$name"

  def extractProducts[A <: ListCategory: ClassTag](
      recipe: DbRecipe
  ): Vector[RecipeProduct] =
    recipe.products.filter(recProduct =>
      recProduct.product.category match
        case _: A => true
        case _    => false
    )
}

