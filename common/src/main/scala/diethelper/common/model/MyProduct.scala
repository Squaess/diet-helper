package diethelper.common.model

final case class MyProduct(name: String, category: ListCategory)

sealed trait ListCategory(val value: String)
case object Fridge extends ListCategory("fridge")
case object Others extends ListCategory("others")
case object Vegetables extends ListCategory("vegetables")

type ProductList = List[MyProduct]