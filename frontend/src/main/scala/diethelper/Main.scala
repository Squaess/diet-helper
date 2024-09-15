package diethelper

import diethelper.common.model.{MyProduct, Fridge}

@main
def frontend = {

  val product = MyProduct("name", Fridge)
  println("product")
  println(s"Using Scala.js version ${System.getProperty("java.jvm.version")}")
}