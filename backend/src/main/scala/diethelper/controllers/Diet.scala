package diethelper.controllers

import cats.effect.IO
import diethelper.services.DietService
import diethelper.services.RedisOperations
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl

object Diet {

  val dsl = Http4sDsl[IO]
  import dsl._

  def getRoutes(redisOperations: RedisOperations[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root =>
      for {
        diet <- req.as[List[(String, Double)]]
        recipes <- DietService.getRecipes(diet.map(_._1), redisOperations)
        x = diet.zip(recipes).map{ case ((_, factor), r) =>
          DietService.multiplyRecipe(r, factor)
        }
        shoppingList = DietService.createShoppingList(x)
        res <- Ok(shoppingList.asJson.noSpaces)
      } yield res
  }
} 