package domain

class RecipeTest extends munit.FunSuite {
  test("id creation") {
    val obtained = Recipe.id("name")
    val expected = "recipe:name"
    assertEquals(obtained, expected)
  }

  test("extractProducts") {
    val products = Vector(
      RecipeProduct(Product("prod1", Fridge), 12),
      RecipeProduct(Product("prod2", Fridge), 23),
      RecipeProduct(Product("prod3", Others), 12),
      RecipeProduct(Product("prod4", Vegetables), 0)
    )
    val rec = Recipe("recipe1", products, "")
    val obtained1 = Recipe.extractProducts[Fridge.type](rec)
    assert(obtained1.length == 2)
    val obtained2 = Recipe.extractProducts[Others.type](rec)
    assert(obtained2.length == 1)
  }
}
