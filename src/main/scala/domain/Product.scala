package domain

case class Product(name: String) extends RedisDocument {

  override def table: String = "product"

  override def id: String = s"$table:$name"

}
