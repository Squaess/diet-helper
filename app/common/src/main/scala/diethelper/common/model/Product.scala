package diethelper.common.model

final case class Product(name: String, category: ListCategory)

sealed trait ListCategory(value: String)
case object Fridge extends ListCategory("fridge")
case object Others extends ListCategory("others")
case object Vegetables extends ListCategory("vegetables")
