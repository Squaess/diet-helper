package domain

final case class Recipe(
    name: String,
    products: Vector[RecipeProduct],
    description: String
) extends RedisDocument {

  override def table: String = Recipe.table

  override def id: String = Recipe.id(name)

}

object Recipe {
  val table = "recipe"

  def id(name: String) = s"$table:$name"
}
