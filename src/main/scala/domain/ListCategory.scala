package domain

sealed trait ListCategory(value: String)
case object Fridge extends ListCategory("fridge")
case object Others extends ListCategory("others")
case object Vegetables extends ListCategory("vegetables")
