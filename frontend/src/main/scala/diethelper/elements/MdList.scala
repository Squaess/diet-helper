package diethelper.elements

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import com.raquo.laminar.api.L.{*}
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveHtmlElement

object MdList {
  @js.native
  trait RawElement extends js.Object{}

  @js.native @JSImport("@material/web/list/list.js", JSImport.Default)
  object RawImport extends js.Object
  RawImport

  type Ref = dom.html.Element & RawElement
  type El = ReactiveHtmlElement[Ref]
  type ModFunction = MdList.type => Mod[El]

  private val tag = htmlTag("md-list")

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(MdList))*)
  }
}

object MdListItem {
  @js.native
  trait RawElement extends js.Object{}

  @js.native @JSImport("@material/web/list/list-item.js", JSImport.Default)
  object RawImport extends js.Object
  RawImport

  type Ref = dom.html.Element & RawElement
  type El = ReactiveHtmlElement[Ref]
  type ModFunction = MdListItem.type => Mod[El]

  private val tag = htmlTag("md-list-item")

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(MdListItem))*)
  }
}
