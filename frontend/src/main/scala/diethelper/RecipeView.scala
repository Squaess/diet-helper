package diethelper

import com.raquo.laminar.api.L.{*, given}
import diethelper.common.model.Recipe
import diethelper.common.model.RecipeList
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import diethelper.common.model.ShoppingList

object RecipeView {

  private type Diet = List[(Recipe, Double)]

  private val allRecipes: Var[RecipeList] = Var(List.empty)
  private val dietPlan: Var[Diet] = Var(List.empty)
  private val dietCalories =
    dietPlan.signal.map(diet => {
      val calories = for {
        (recipe, factor) <- diet
      } yield recipe.calories * factor
      calories.sum.toString
    })

  def setRecipes(recipes: RecipeList) = {
    allRecipes.set(recipes)
  }

  def appElement() = {
    div(
      diet(dietPlan.signal),
      summary(),
      refreshAllRecipesButton,
      recipes(allRecipes.signal)
    )
  }

  def summary() = {
    div(h3(child.text <-- dietCalories))
  }

  def refreshAllRecipesButton = {
    button(
      "🗘",
      onClick.preventDefault.flatMap(_ =>
        FetchStream.get("http://localhost:8080/recipe")
      ) --> { responseText =>
        decode[RecipeList](responseText) match
          case Left(value)  => dom.window.alert(value.getMessage())
          case Right(value) => setRecipes(value)
      }
    )
  }

  def recipes(recipesSignal: Signal[RecipeList]) = {
    table(
      tbody(
        children <-- recipesSignal.split(_.name) {
          (id, initial, recipeSignal) =>
            renderRecipe(id, recipeSignal, initial)
        }
      )
    )
  }

  def renderRecipe(
      name: String,
      recipeSignal: Signal[Recipe],
      recipe: Recipe
  ): HtmlElement = {
    tr(
      td(child.text <-- recipeSignal.map(_.name)),
      td(child.text <-- recipeSignal.map(_.steps)),
      td(child.text <-- recipeSignal.map(_.calories)),
      td(addProductToDietButton(recipe))
    )
  }

  def buildTmpDisplayList(value: ShoppingList): String = {
    val sep = "#" * 10
    s"""
    |Inne:
    |${value.others.map{ case (prod, count) => s"${prod.name} ${count}"}.mkString("\n")}
    |
    |
    |$sep
    |
    |Lodówka:
    |${value.fridge.map{ case (p, c) => s"${p.name} $c"}.mkString("\n")}
    |
    |$sep
    |
    |Warzywa:
    |${value.vegetables.map{ case (p, c) => s"${p.name} $c"}.mkString("\n")}
    """.stripMargin
  }

  def diet(dietSignal: Signal[Diet]) = {
    div(
      table(
        tbody(
          children <-- dietSignal.split(_._1.name) {
            (id, initial, dietSignal) =>
              renderDiet(id, dietSignal)
          }
        )
      ),
      button(
        "Generate",
        onClick.preventDefault.flatMap { _ =>
          FetchStream.post(
            "http://localhost:8080/diet",
            _.body(dietPlan.now().asJson.noSpaces)
          )
        } --> (response => 
          decode[ShoppingList](response) match
            case Left(value) => dom.window.alert("Something went wrong. Please contact some1 :)")
            case Right(value) =>
              dom.window.alert(buildTmpDisplayList(value))
          
        )
      )
    )
  }

  def renderDiet(name: String, vals: Signal[(Recipe, Double)]) = {
    tr(
      td(child.text <-- vals.map(_._1.name)),
      td(
        input(
          typ := "number",
          controlled(
            value <-- vals.map(_._2.toString),
            onInput.mapToValue.map(_.toDoubleOption).collect {
              case Some(value) => (name, value)
            } --> (newValue =>
              dietPlan.update(diet =>
                diet.map { item =>
                  if item._1.name == newValue._1 then {
                    item.copy(_2 = newValue._2)
                  } else item
                }
              )
            )
          )
        )
      ),
      td(
        button("🗑️", onClick --> (_ => removeRecipeFromDiet(name)))
      ),
      td(p(text <-- vals.map { case (recipe, factor) =>
        recipe.calories * factor
      }))
    )
  }

  def addProductToDietButton(recipe: Recipe) = {
    button(
      "➕",
      onClick --> (_ => addRecipeToDiet(recipe))
    )
  }

  def addRecipeToDiet(recipe: Recipe): Unit = {
    dietPlan.update(diet =>
      if diet.map(_._1.name).contains(recipe.name) then diet
      else (recipe, 1) +: diet
    )
  }

  def removeRecipeFromDiet(name: String): Unit = {
    dietPlan.update(diet => diet.filterNot(_._1.name == name))
  }
}
