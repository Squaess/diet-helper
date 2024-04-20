package services

import domain._
import services.Recipes.reduceProductsInCategory
import services.Recipes.createShoppingList

class RecipesTest extends munit.FunSuite {
  test("reduceProductsInCategory") {
    val testData = Vector(
      RecipeProduct(products("prod1"), 1),
      RecipeProduct(products("prod2"), 1),
      RecipeProduct(products("prod1"), 1)
    )
    val prod1Obtained =
      reduceProductsInCategory(testData).filter(_.product.name == "prod1")
    assertEquals(reduceProductsInCategory(testData).length, 2)
    assertEquals(prod1Obtained.length, 1)
    assertEquals(prod1Obtained.head.quantity, 2.0)
  }

  test("createShoppingList") {
    val obtained = createShoppingList(testRecipes)
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
    "prod1" -> Product("prod1", Fridge),
    "prod2" -> Product("prod2", Fridge),
    "prod3" -> Product("prod3", Others),
    "prod4" -> Product("prod4", Others),
    "prod5" -> Product("prod5", Vegetables),
    "prod6" -> Product("prod6", Vegetables)
  )

  private val testRecipes = Vector(
    Recipe(
      name = "rec1",
      products = Vector(
        RecipeProduct(products("prod1"), 1),
        RecipeProduct(products("prod2"), 2),
        RecipeProduct(products("prod3"), 3)
      ),
      description = ""
    ),
    Recipe(
      name = "rec2",
      products = Vector(
        RecipeProduct(products("prod2"), 1),
        RecipeProduct(products("prod3"), 2),
        RecipeProduct(products("prod4"), 3)
      ),
      description = ""
    ),
    Recipe(
      name = "rec3",
      products = Vector(
        RecipeProduct(products("prod3"), 1),
        RecipeProduct(products("prod4"), 2),
        RecipeProduct(products("prod5"), 3)
      ),
      description = ""
    )
  )
}
