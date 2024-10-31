//> using toolkit default

import os.*
import ujson.*

val jsonPath = RelPath(args(0))

val json: Array[ujson.Value] =
  os.read(os.pwd / jsonPath).split("\n").map(x => ujson.read(x))
json.foreach { j =>
  val res =
    os.proc("products/save_product.sh", j("name").str, j("category").str).call()
  res.out.lines().foreach(println)
}
