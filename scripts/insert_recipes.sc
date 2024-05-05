//> using toolkit default

import os.*
import ujson.*

val jsonDir = RelPath(args(0))

if (os.isFile(pwd / jsonDir)) {
  val procCall = os.proc("recipes/save_recipe.sh", (pwd / jsonDir).toString)
    .call()
  (procCall.err.text().length() > 0)
} else os.list(pwd / jsonDir)
  .foreach(jsonLocation => {
    println(jsonLocation)
    val procCall = os.proc("recipes/save_recipe.sh", jsonLocation.toString)
      .call()
    println(procCall.out.text())
  }
  )
