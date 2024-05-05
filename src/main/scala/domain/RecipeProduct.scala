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

final case class RecipeProductDSL(name: String, quantity: Double)
