package diethelper.services

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.web.FetchStream
import diethelper.common.model.*
import io.circe.parser.decode
import io.circe.generic.auto.*
import io.circe.syntax.*

trait ServerApi {
  def getProducts: EventStream[ProductList]
  def postProduct(product: MyProduct): EventStream[String]
  def deleteProduct(name: String): EventStream[String]

  def getRecipes: EventStream[String]
  def postRecipe(recipe: Recipe): EventStream[String]
  def postDiet(diet: Diet): EventStream[String]
}

object ServerApi {
  val serverApiImpl = (baseUrl: String) =>
    new ServerApi {

      override def getProducts: EventStream[ProductList] = FetchStream
        .get(s"$baseUrl/product")
        .map(response =>
          decode[ProductList](response) match
            case Left(value)  => List.empty // TODO: better handle this
            case Right(value) => value
        )
      override def postProduct(product: MyProduct): EventStream[String] =
        FetchStream.post(
          s"$baseUrl/product",
          _.body(product.asJson.noSpaces)
        )
      override def deleteProduct(name: String): EventStream[String] =
        FetchStream.apply(_.DELETE, s"$baseUrl/product/$name")

      override def getRecipes: EventStream[String] = FetchStream.get(s"$baseUrl/recipe")

      override def postRecipe(recipe: Recipe): EventStream[String] = {
        FetchStream.post(
          s"$baseUrl/recipe",
          _.body(recipe.asJson.noSpaces)
        )
      }

      override def postDiet(diet: Diet): EventStream[String] = {
        FetchStream.post(
          s"$baseUrl/diet",
          _.body(diet.asJson.noSpaces)
        )
      }
    }
}
