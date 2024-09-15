package diethelper

import com.raquo.laminar.api.L.{*, given}
// import diethelper.common.model.Fridge

final class ProductModel {
  val dataVar: Var[List[String]] = Var(List.empty)
  val dataSignal = dataVar.signal

  def addProduct(product: String): Unit =
    dataVar.update(data => {
      data :+ product
    })

  def removeProduct(product: String): Unit =
    dataVar.update(data => data.filter(_ != product))

}

object ProductModel {
  val productModel = new ProductModel

  def appElement() =
    div(h1("Product page??"), renderTable())

  def renderTable(): Element = {
    table(
      thead(tr(th("Name"), th("Category"), th("Action"))),
      tbody(
        children <-- productModel.dataSignal.map(data =>
          data.map { product => renderProductItem(product) }
        )
      ),
      tfoot(
        tr(
          td(
            button(
              "➕",
              onClick --> (_ =>
                productModel.addProduct("test")
              )
            )
          ),
          td(),
          td(
            child.text <-- productModel.dataSignal.map(data =>
              data.reduce(_ + _)
            )
          )
        )
      )
    )
  }

  def renderProductItem(item: String): Element = {
    tr(
      td(item),
      td(item),
      td(button("🗑️", onClick --> (_ => productModel.removeProduct(item))))
    )
  }
}
