package domain

final case class Recipe(
    name: String,
    products: Vector[RecipeProduct],
    description: String
) extends RedisDocument {

  override def table: String = Recipe.table

  override def id: String = Recipe.id(name)

}

object Recipe {
  val table = "recipe"

  def id(name: String) = s"$table:$name"
}

final case class RecipeProduct(product: Product, quantity: Quantity)

final case class Quantity(si: Si, other: Option[CommonUnit])

enum Si(value: Double):
  case gram(value: Double) extends Si(value)
  case ml(value: Double) extends Si(value)

enum CommonUnit(value: Double):
  case Glass(value: Double) extends CommonUnit(value)
  case TableSpoon(value: Double) extends CommonUnit(value)
  case TeaSpoon(value: Double) extends CommonUnit(value)
