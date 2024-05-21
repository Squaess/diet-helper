package diethelper.domain.db

case class Product(name: String, category: ListCategory) extends RedisDocument {

  override def table: String = Product.table

  override def id: String = Product.id(name)

}

object Product {
  val table = "product"

  def id(name: String) = s"$table:$name"
}
