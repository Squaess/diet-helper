package domain

case class Product(name: String) extends RedisDocument {

  override def id: String = name

  override def table: String = "product"

}
