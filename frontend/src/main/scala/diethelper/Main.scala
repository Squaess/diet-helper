package diethelper

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import diethelper.elements.TestElements

@main
def frontend = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    // AppPage.app
    TestElements.show
  )
}