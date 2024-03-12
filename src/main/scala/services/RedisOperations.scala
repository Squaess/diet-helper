package services

import cats.effect.IO
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout.given
import scala.languageFeature.existentials

trait RedisOperations {
  def getById(id: String): IO[Option[String]]

  def setById(id: String, value: String): IO[Unit]
}

object RedisOperations {

  object Impl extends RedisOperations {

    override def getById(id: String): IO[Option[String]] =
      // TODO: use fromClient instead
      Redis[IO].utf8("redis://localhost").use { redis =>
        for x <- redis.get(id)
        yield x
      }

    override def setById(id: String, value: String): IO[Unit] =
      Redis[IO].utf8("redis://localhost").use { redis =>
        for _ <- redis.set(id, value)
        yield ()
      }
  }

}
