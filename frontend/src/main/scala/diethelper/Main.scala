package diethelper

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

@main
def frontend = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    // MainPage.appElement()
    ProductPage.appElement()
  )
}