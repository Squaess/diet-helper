package diethelper

import com.raquo.laminar.api.L.{*, given}
import diethelper.common.model.{ProductList, Fridge, MyProduct, MyProductID}

final class ProductPage {
  val dataVar: Var[ProductList] = Var(List.empty)
  val dataSignal = dataVar.signal

  def addProduct(product: MyProduct): Unit =
    dataVar.update(data => {
      data :+ product
    })

  def removeProduct(product: MyProduct): Unit =
    dataVar.update(data => data.filter(_.id != product.id))

}

object ProductPage {
  val productModel = new ProductPage

  def appElement() = {
    div(
      h1("Product page"),
      renderTable(),
      renderDataList()
    )
  }

  def renderTable() = {
    table(
      thead(tr(th("Name"), th("Category"), th("Action"))),
      tbody(
        children <-- productModel.dataSignal.split(_.id) { (id, initial, itemSignal) =>
          renderProductItem(initial, itemSignal)
        }
      ),
      tfoot(
        tr(
          td(
            button(
              "➕",
              onClick --> (_ =>
                productModel.addProduct(MyProduct(MyProductID(), "test", Fridge))
              )
            )
          ),
          td(),
          td()
        )
      )
    )
  }

  def renderProductItem(item: MyProduct, itemSignal: Signal[MyProduct]) = {
    tr(
      td(
        input(
          typ := "text",
          value <-- itemSignal.map(_.name),
          onInput.mapToValue --> { (newName: String) => productModel.dataVar.update{ data => data.map { existingItem => if existingItem.id == item.id then existingItem.copy(name = newName) else existingItem}}}
        )
        // child.text <-- itemSignal.map(_.name)
      ),
      td(child.text <-- itemSignal.map(_.category.value)),
      td(button("🗑️", onClick --> (_ => productModel.removeProduct(item))))
    )
  }

  def renderDataList() = {
    ul(
      children <-- productModel.dataSignal.split(_.id) { (id, initial, dataSignal) =>
        li(child.text <-- dataSignal.map(item => s"${item.name} ==> ${item.category.value}"))
      }
    )
  }
}
