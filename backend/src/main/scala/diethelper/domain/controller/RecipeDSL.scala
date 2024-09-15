package diethelper.domain.controller

final case class RecipeDSL(
    name: String,
    products: Vector[RecipeProductDSL],
    calories: Double,
    description: String
)
