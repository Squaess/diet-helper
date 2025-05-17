package diethelper.elements

import com.raquo.laminar.api.L.{*}
import com.raquo.laminar.tags.HtmlTag
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom
import org.scalajs.dom.HTMLButtonElement
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object FilledButton {

  @js.native
  trait RawElement extends js.Object {}

  @js.native @JSImport("@material/web/button/filled-button.js", JSImport.Default)
  object RawImport extends js.Object
  RawImport // needed because objects are lazy

  type Ref = dom.html.Element & RawElement
  type El = ReactiveHtmlElement[Ref]
  type ModFunction = FilledButton.type => Mod[El]

  private val tag = htmlTag("md-filled-button")

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(FilledButton))*)
  }
  
}
