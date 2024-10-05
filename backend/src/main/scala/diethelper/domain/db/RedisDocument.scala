package diethelper.domain.db

import diethelper.common.model.MyProduct
import diethelper.common.model.Recipe

trait RedisDocument[T] {
  def table: String
  def id(entity: T): String
  def id(name: String): String
}

object RedisDocument {
  implicit val productRedisDocument: RedisDocument[MyProduct] =
    new RedisDocument {
      override def table: String = "product"
      override def id(entity: MyProduct): String = s"$table:${entity.name}"
      override def id(name: String): String = s"$table:$name"
    }

  implicit val recipeRedisDocument: RedisDocument[Recipe] = new RedisDocument {
    override def table: String = "recipe"
    override def id(entity: Recipe): String = s"$table:${entity.name}"
    override def id(name: String): String = s"$table:$name"
  }
}
