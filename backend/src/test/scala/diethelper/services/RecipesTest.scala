package diethelper.services

import diethelper.domain.db
import diethelper.domain.db.*

class RecipesTest extends munit.FunSuite {
  test("reduceProductsInCategory") {
    val testData = Vector(
      RecipeProduct(products("prod1"), 1),
      RecipeProduct(products("prod2"), 1),
      RecipeProduct(products("prod1"), 1)
    )
    val prod1Obtained =
      Recipes.reduceProductsInCategory(testData).filter(_.product.name == "prod1")
    assertEquals(Recipes.reduceProductsInCategory(testData).length, 2)
    assertEquals(prod1Obtained.length, 1)
    assertEquals(prod1Obtained.head.quantity, 2.0)
  }

  test("createShoppingList") {
    val obtained = Recipes.createShoppingList(testRecipes)
    assertEquals(obtained.fridge.length, 2)
    assertEquals(obtained.others.length, 2)
    assertEquals(obtained.vegetables.length, 1)

    assertEquals(obtained.fridge.filter(_.product.name == "prod1").length, 1)
    assertEquals(
      obtained.fridge.filter(_.product.name == "prod1").head.quantity,
      1.0
    )

    assertEquals(obtained.fridge.filter(_.product.name == "prod2").length, 1)
    assertEquals(
      obtained.fridge.filter(_.product.name == "prod2").head.quantity,
      3.0
    )
  }

  private val products = Map(
    "prod1" -> db.Product("prod1", Fridge),
    "prod2" -> db.Product("prod2", Fridge),
    "prod3" -> db.Product("prod3", Others),
    "prod4" -> db.Product("prod4", Others),
    "prod5" -> db.Product("prod5", Vegetables),
    "prod6" -> db.Product("prod6", Vegetables)
  )

  private val testRecipes = Vector(
    Recipe(
      name = "rec1",
      products = Vector(
        RecipeProduct(products("prod1"), 1),
        RecipeProduct(products("prod2"), 2),
        RecipeProduct(products("prod3"), 3)
      ),
      calories = 12,
      description = ""
    ),
    Recipe(
      name = "rec2",
      products = Vector(
        RecipeProduct(products("prod2"), 1),
        RecipeProduct(products("prod3"), 2),
        RecipeProduct(products("prod4"), 3)
      ),
      calories = 12,
      description = ""
    ),
    Recipe(
      name = "rec3",
      products = Vector(
        RecipeProduct(products("prod3"), 1),
        RecipeProduct(products("prod4"), 2),
        RecipeProduct(products("prod5"), 3)
      ),
      calories = 12,
      description = ""
    )
  )
}
