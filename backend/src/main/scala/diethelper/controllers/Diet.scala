package diethelper.controllers

import cats.effect.IO
import diethelper.services.DietService
import diethelper.services.RedisOperations
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl
import diethelper.common.model.Diet

object Diet {

  val dsl = Http4sDsl[IO]
  import dsl._

  def getRoutes(redisOperations: RedisOperations[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root =>
      for {
        diet <- req.attemptAs[Diet].value
        res <- diet match {
          case Left(value) => BadRequest("Cannot deserialize diet.")
          case Right(diet) => {
            val scaledRecipes = diet.map(DietService.multiplyRecipe)
            val shoppingList = DietService.createShoppingList(scaledRecipes)
            Ok(shoppingList.asJson.noSpaces)
          }
        }
      } yield res
  }
} 