package diethelper

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.keys.HtmlAttr
import com.raquo.laminar.codecs.StringAsIsCodec
import diethelper.common.model.{ProductList, MyProduct, ListCategory}
import org.scalajs.dom
import io.circe.syntax.*
import io.circe.generic.auto.*
import io.circe.parser.decode

object ProductPage {

  val allProducts: Var[ProductList] = Var(List.empty)

  def updateAllProducts(products: ProductList): Unit = {
    allProducts.set(products)
  }

  def appElement() = {
    div(
      h1("Input product"),
      renderProductInput,
      h1("All Products"),
      renderCurrentProducts
    )
  }

  def renderProductInput = {
    table(
      thead(tr(td("Name"), td("Category"), td("Save"))),
      tbody(
        tr(
          td(
            input(
              typ := "text",
              idAttr := "name"
            )
          ),
          td(inputForCategory),
          td(
            button(
              "➤",
              onClick.preventDefault --> { (_) =>
                val name = dom.document
                  .getElementById("name")
                  .asInstanceOf[dom.HTMLInputElement]
                  .value
                val category = dom.document
                  .getElementById("category")
                  .asInstanceOf[dom.HTMLSelectElement]
                  .value
                dom.window.alert(s"Submit ($name, $category)")
              }
            )
          )
        )
      )
    )
  }

  def inputForCategory = {
    val options = ListCategory.values
      .map(cat => option(cat.toString, value := cat.value))
    select(
      options*
    ).amend(idAttr := "category")

  }

  def renderCurrentProducts = {
    table(
      thead(tr(td("Product name"), td("Category"))),
      tbody(
        children <-- allProducts.signal.split(_.name) {
          (name, initial, signal) => renderProduct(signal)
        }
      ),
      tfoot(
        tr(
          td(
            button(
              "🗘",
              onClick.flatMap(_ =>
                FetchStream.get("http://localhost:8080/product")
              ) --> { responseText =>
                decode[ProductList](responseText) match
                  case Left(value)  => dom.window.alert(value.getMessage())
                  case Right(value) => updateAllProducts(value)
              }
            )
          )
        )
      )
    )
  }

  def renderProduct(productSignal: Signal[MyProduct]) = {
    tr(
      td(child.text <-- productSignal.map(_.name)),
      td(child.text <-- productSignal.map(_.category.value))
    )
  }

}
