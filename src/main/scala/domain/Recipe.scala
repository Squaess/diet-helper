package domain

final case class Recipe(
    name: String,
    products: Vector[RecipeProduct],
    description: String
)

final case class RecipeProduct(product: Product, quantity: Quantity)

final case class Quantity(si: Si, other: CommonUnit)

enum Si(name: String, value: Double):
  case gram(value: Double) extends Si("g", value)
  case ml(value: Double) extends Si("ml", value)

enum CommonUnit(name: String, value: Double):
  case Glass(value: Double) extends CommonUnit("glass", value)
  case TableSpoon(value: Double) extends CommonUnit("tablespoon", value)
  case TeaSpoon(value: Double) extends CommonUnit("teaspoon", value)
