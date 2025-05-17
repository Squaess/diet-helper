package diethelper.elements

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.codecs.{BooleanAsAttrPresenceCodec, StringAsIsCodec}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.tags.HtmlTag

object TestElements {

  def leftPane = div(
    idAttr:="leftPane",
    MdList(
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello"),
      _ => MdListItem(_ => "Hello")
    )
  )

  def righPane = div(
    idAttr:="rightPane",
    FilledButton(
      _ => "Hello",
      _ => onClick --> (_ => dom.window.alert("Click"))
    )
  )

  def show = {
    div(
      h1("Diet Helper"),
      leftPane,
      righPane
    )
  }

}
