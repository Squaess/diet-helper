package diethelper

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.keys.HtmlAttr
import com.raquo.laminar.codecs.StringAsIsCodec
import diethelper.common.model.{ProductList, MyProduct, ListCategory}
import org.scalajs.dom
import io.circe.syntax.*
import io.circe.generic.auto.*
import io.circe.parser.decode
import diethelper.services.ServerApi

class ProductPage(serverApi: ServerApi) {

  val allProducts: Var[ProductList] = Var(List.empty)

  def setAllProducts(products: ProductList): Unit = {
    allProducts.set(products)
  }

  def deleteFromAllProducts(name: String): Unit = {
    allProducts.update(products => products.filter(p => p.name != name))
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
              onClick.preventDefault.flatMap { (_) =>
                val name = dom.document
                  .getElementById("name")
                  .asInstanceOf[dom.HTMLInputElement]
                  .value
                val category = dom.document
                  .getElementById("category")
                  .asInstanceOf[dom.HTMLSelectElement]
                  .value
                val productToInsert =
                  MyProduct(name, ListCategory.valueOf(category))
                // FetchStream.post(
                //   "http://localhost:8080/product",
                //   _.body(productToInsert.asJson.noSpaces)
                // )
                serverApi.postProduct(productToInsert)
              } --> { response =>
                dom.window.alert(response)
              }
            )
          )
        )
      )
    )
  }

  def inputForCategory = {
    val options = ListCategory.values
      .map(cat => option(cat.toString))
    select(
      options*
    ).amend(idAttr := "category")

  }

  def renderCurrentProducts = {
    table(
      thead(tr(td("Product name"), td("Category"))),
      tbody(
        children <-- allProducts.signal.split(_.name) {
          (name, initial, signal) => renderProduct(name, signal)
        }
      ),
      tfoot(
        tr(
          td(
            button(
              "🗘",
              onClick.flatMap(_ =>
                // FetchStream.get("http://localhost:8080/product")
                serverApi.getProducts
              ) --> { responseText =>
                responseText match
                  case Nil  => dom.window.alert("Couldn't retrieve products.")
                  case x :: xs => setAllProducts(responseText)
              }
            )
          )
        )
      )
    )
  }

  def renderProduct(name: String, productSignal: Signal[MyProduct]) = {
    tr(
      td(child.text <-- productSignal.map(_.name)),
      td(child.text <-- productSignal.map(_.category.toString())),
      td(
        button(
          "❌",
          onClick.flatMap(_ =>
            // FetchStream.apply(_.DELETE, s"http://localhost:8080/product/$name")
            serverApi.deleteProduct(name)
          ) --> { response =>
            dom.window.alert(response)
            deleteFromAllProducts(name)
          }
        )
      )
    )
  }
}
