package diethelper

import diethelper.common.model.{MyProduct}
import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

@main
def frontend = {
  val product = MyProduct("name", "Fridge")
  println("product")
  println(s"Using Scala.js version ${System.getProperty("java.jvm.version")}")
  renderOnDomContentLoaded(dom.document.getElementById("app"), MainPage.appElement())
}