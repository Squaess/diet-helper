package diethelper.views

import scalatags.Text
import scalatags.Text.all._
import diethelper.views.htmx.HtmxAttributes

object ProductView {
  def newProductRow: Text.TypedTag[String] = {
    tr(
      td(input(name := "name")),
      td(input(name := "quantity")),
      td(button(HtmxAttributes.get("/product/empty"), HtmxAttributes.swap("outerHTML"), "X"))
    )
  }

  def empty = ""
}
