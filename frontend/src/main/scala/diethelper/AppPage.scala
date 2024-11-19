package diethelper

import com.raquo.laminar.api.L.{*, given}
import diethelper.services.ServerApi

sealed trait Page
case object Product extends Page
case object Recipe extends Page
case object Diet extends Page

object AppPage {

  val serverApi = ServerApi.serverApiImpl("/api")
  // val serverApi = ServerApi.serverApiImpl("http://localhost:8080")
  // This is probably not the bet implementation but
  // to be honest who cares? This is just a simple and
  // small app.
  val currentPage = Var[Page](Diet)

  def renderPage(page: Page) = page match
    case Product => ProductPage(serverApi).appElement()
    case Recipe  => RecipePage(serverApi).appElement()
    case Diet    => RecipeView(serverApi).appElement()

  val navBar = div(
    idAttr := "nav-bar",
    ul(
      li(
        button("Product", onClick.mapTo(Product) --> currentPage.writer),
        className := "card"
      ),
      li(
        button("Recipe", onClick.mapTo(Recipe) --> currentPage.writer),
        className := "card"
      ),
      li(button("Diet", onClick.mapTo(Diet) --> currentPage.writer), className := "card")
    )
  )

  val app = div(
    navBar,
    h1("Diet helper!"),
    child <-- currentPage.signal.map(renderPage)
  )
}
