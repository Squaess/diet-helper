package diethelper

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

@js.native @JSImport("/javascript.svg", JSImport.Default)
val javascriptLogo: String = js.native

@main
def frontend = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    MainPage.appElement()
  )
}

def setupCounter(element: dom.Element): Unit = {
  var counter = 0

  def setCounter(count: Int): Unit =
    counter = count
    element.innerHTML = s"count is $counter"

  element.addEventListener("click", e => setCounter(counter + counter))
  setCounter(1)
}

object MainPage:
  def appElement() =
    div(
      a(
        href := "https://vitejs.dev",
        target := "_blank",
        img(src := "/vite.svg", className := "logo", alt := "Vite Logo")
      ),
      a(
        href := "https://developer.mozilla.org/en-US/docs/Web/JavaScript",
        target := "_blank",
        img(
          src := javascriptLogo,
          className := "logo vanilla",
          alt := "JavaScript logo"
        )
      ),
      h1("Hello Friend"),
      div(className := "card", counterButton()),
      p(className := "read-the-docs", "Click on the Vite logo to learn more")
    )

  def counterButton(): Element = {
    val counter = Var(1)
    button(
      tpe := "button",
      "count: ",
      child.text <-- counter,
      onClick --> {event => counter.update(c => c + c)}
    )
  }
