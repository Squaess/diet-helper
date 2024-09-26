package diethelper.common.model

final class MyProductID
final case class MyProduct(name: String, category: ListCategory)

enum ListCategory(val value: String) {
  case Fridge extends ListCategory("fridge")
  case Others extends ListCategory("others")
  case Vegetables extends ListCategory("vegetables")
}

type ProductList = List[MyProduct]
