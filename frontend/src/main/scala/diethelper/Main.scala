package diethelper

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

// TODO: replace with some config provider
object Config {
  val backendHost: String = "backend"
  val backendPort: Int = 8080
}

@main
def frontend = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    // LaminarExample.appElement(),
    // ProductPage.appElement()
    // RecipePage.appElement()
    RecipeView.appElement()
  )
}