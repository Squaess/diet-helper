package diethelper.common.model

import scala.util.Random

final case class MyProduct(name: String, category: ListCategory)

enum ListCategory {
  case Fridge extends ListCategory
  case Others extends ListCategory
  case Vegetables extends ListCategory

}

object ListCategory {

  def randomCategory: ListCategory = {
    val m = ListCategory.values.length
    ListCategory.values(Random.nextInt(m))
  }
}

type ProductList = List[MyProduct]
