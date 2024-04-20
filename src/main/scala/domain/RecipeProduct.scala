package domain

final case class RecipeProduct(product: Product, quantity: Double) {
  def +(that: RecipeProduct): RecipeProduct = {
    require(that.product.id == product.id)
    RecipeProduct(
      product,
      quantity + that.quantity
    )
  }
}

// Disable for now, implement later as a feature
// final case class Quantity(si: Si) {
//   def +(that: Quantity): Quantity = {
//     require(that.si.isInstanceOf[si.type])
//     Quantity(si, )
//   }
// }

// enum Si(value: Double):
//   case gram(value: Double) extends Si(value)
//   case ml(value: Double) extends Si(value)

// enum CommonUnit(value: Double):
//   case Glass(value: Double) extends CommonUnit(value)
//   case TableSpoon(value: Double) extends CommonUnit(value)
//   case TeaSpoon(value: Double) extends CommonUnit(value)
