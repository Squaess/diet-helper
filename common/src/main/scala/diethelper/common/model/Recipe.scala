package diethelper.common.model

final case class Recipe(
    name: String,
    steps: String,
    products: RecipeProducts,
    calories: Double
)

type RecipeProduct = Tuple2[MyProduct, Double]
type RecipeProducts = List[RecipeProduct]
