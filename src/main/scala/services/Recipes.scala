package services

import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityDecoder._
import domain.Recipe
import cats.effect.IO

object Recipes {

  val redisOperation = RedisOperations.Impl

  def getRecipe(name: String) = ???

  def saveRecipe(recipe: Recipe) = for {
    allSaved <- checkProducts(recipe)
    _ <- if (allSaved) then redisOperation.save[Recipe](recipe) else IO.unit
  } yield ()

  private def checkProducts(recipe: Recipe): IO[Boolean] = {
    for {
      res <- recipe.products
        .map(prod => redisOperation.get[domain.Product](prod.product.id))
        .sequence
    } yield res
      .map(_.isDefined)
      .map(x =>
        if !x then IO.println("Not in db")
        x
      )
      .reduce(_ && _)
  }
}
