package diethelper.domain.db

final case class ShoppingList(
    others: Vector[RecipeProduct],
    fridge: Vector[RecipeProduct],
    vegetables: Vector[RecipeProduct]
)
