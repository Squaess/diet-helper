package diethelper

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import com.raquo.laminar.api.L.{*, given}

@js.native @JSImport("/javascript.svg", JSImport.Default)
val javascriptLogo: String = js.native

object MainPage {
  def appElement() = {
    div(
      a(
        href := "https://vitejs.dev",
        target := "_blank",
        img(src := "/vite.svg", className := "logo", alt := "Vite Logo")
      ),
      a(
        href := "https://developer.mozilla.org/en-US/docs/Web/JavaScript",
        target := "_blank",
        img(src := javascriptLogo, className := "logo vanilla", alt := "JavaScript logo")
      ),
      h1("Hello ScalaJS"),
      div(className := "card", counterButton()),
      p(className := "read-the-docs", "Click on the Vite logo to learn more")
    )
  }

  def counterButton() = {
    val counter = Var(0)
    button(
      tpe := "button",
      "count: ",
      child.text <-- counter,
      onClick --> {event => counter.update(c => c + 1)}
    )
  }
}
