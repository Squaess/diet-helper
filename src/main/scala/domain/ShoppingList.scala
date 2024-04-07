package domain

final case class ShoppingList(
    others: Vector[RecipeProduct],
    fridge: Vector[RecipeProduct],
    vegetables: Vector[RecipeProduct]
)
