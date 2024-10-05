package diethelper

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import diethelper.common.model.{
  RecipeProducts,
  Recipe,
  RecipeProduct,
  MyProduct,
  ListCategory,
  ProductList
}
import scala.util.Random
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.generic.auto.*

object RecipePage {

  final class ProductInputID

  private val allProducts: Var[ProductList] = Var(List.empty)

  private val productInputs: Var[List[ProductInputID]] = Var(List.empty)

  private def setAllProducts(products: ProductList): Unit = {
    allProducts.set(products)
  }

  private def addProductInput = {
    productInputs.update(inputIds => inputIds :+ ProductInputID())
  }

  private def removeProductInput(productInputId: ProductInputID) = {
    productInputs.update(inputIds => inputIds.filter(_ != productInputId))
  }

  def appElement() = {
    div(
      h1("Input recipe"),
      button("refresh", refreshAllProducts),
      renderRecipeInput,
      productsDataList
    )
  }

  def renderRecipeInput = {
    div(
      table(
        tbody(
          tr(
            td("Name:"),
            td(input(typ := "text", idAttr := "recipe-name"))
          ),
          tr(
            td("Steps:"),
            td(textArea(idAttr := "recipe-steps"))
          ),
          tr(
            td("Calories:"),
            td(input(typ := "text", idAttr := "recipe-calories"))
          ),
          children <-- productInputs.signal.split(id => id) {
            (id, initial, signal) => renderProductInput(id)
          }
        ),
        tfoot(
          tr(
            td("Add product"),
            td(button("➕", onClick --> (_ => addProductInput)))
          )
        )
      ),
      button(
        "save",
        onClick.preventDefault.map{ (_) =>
          val reciepName = dom.document
            .getElementById("recipe-name")
            .asInstanceOf[dom.HTMLInputElement]
            .value
          val recipeSteps = dom.document
            .getElementById("recipe-steps")
            .asInstanceOf[dom.HTMLTextAreaElement]
            .value
          val calories = dom.document
            .getElementById("recipe-calories")
            .asInstanceOf[dom.HTMLInputElement]
            .value
            .toDouble
          val productNames = dom.document
            .querySelectorAll(".product-names")
            .asInstanceOf[dom.NodeList[dom.HTMLInputElement]]
          val productMasses = dom.document
            .querySelectorAll(".product-masses")
            .asInstanceOf[dom.NodeList[dom.HTMLInputElement]]
          val recipeProducts: RecipeProducts = productNames
            .zip(productMasses)
            .map((name, mass) => {
              val convMap = allProducts.now().map(p => (p.name -> p)).toMap
              (convMap(name.value), mass.value.toDoubleOption.getOrElse(0.0))
            })
            .toList
          val recipe = Recipe(reciepName, recipeSteps, recipeProducts, calories)
          recipe
        }.flatMap{ (rec) =>
          FetchStream.post(
            "http://localhost:8080/recipe",
            _.body(rec.asJson.noSpaces)
          ) 
        } --> {responseText => dom.console.log(responseText)}
      )
    )
  }

  def renderProductInput(inputId: ProductInputID) = {
    tr(
      td(input(listId := "products", className := "product-names")),
      td(
        input(
          typ := "number",
          stepAttr := "0.01",
          minAttr := "0",
          defaultValue := "0",
          className := "product-masses"
        )
      ),
      td(
        button(
          "❌",
          onClick --> { (_) => removeProductInput(inputId) }
        )
      )
    )
  }

  def productsDataList = {
    dataList(
      idAttr := "products",
      children <-- allProducts.signal.split(_.name) {
        (name, initial, itemSignal) =>
          option(value <-- itemSignal.map(_.name))
      }
    )
  }

  private def refreshAllProducts = {
    onClick.flatMap(_ => FetchStream.get("http://localhost:8080/product")) --> {
      responseText =>
        decode[ProductList](responseText) match {
          case Left(value)  => dom.console.log(value.getMessage())
          case Right(value) => setAllProducts(value)
        }
    }
  }
}
