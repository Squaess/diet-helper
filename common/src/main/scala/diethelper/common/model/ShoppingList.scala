package diethelper.common.model

final case class ShoppingList(
    others: List[RecipeProduct],
    fridge: List[RecipeProduct],
    vegetables: List[RecipeProduct]
)

