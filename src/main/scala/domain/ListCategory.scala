package domain

enum ListCategory(value: String):
  case Fridge extends ListCategory("fridge")
  case Others extends ListCategory("others")
  case Vegetables extends ListCategory("vegetables")