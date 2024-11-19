package diethelper.services

import diethelper.common.model.Recipe
import io.circe.generic.auto.*
import cats.implicits.*
import cats.effect.Sync

class RecipeService[F[_]: Sync](repo: RedisOperations[F]) {

  def listRecipes: F[List[Recipe]] = {
    for {
      keys <- repo.list[Recipe]
      recipes <- keys
        .map(repo.get[Recipe])
        .traverse(_.map(_.toList))
        .map(_.flatten)
    } yield recipes
  }

  def getRecipe(name: String): F[Option[Recipe]] = repo.get[Recipe](name)

  def deleteRecipe(name: String): F[Long] = repo.delete[Recipe](name)

  // TODO: validate if the products exists in the database
  def saveRecipe(recipe: Recipe): F[Unit] = repo.save(recipe)
}
