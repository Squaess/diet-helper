package services

import cats.effect.IO
import cats.effect.kernel.Sync
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log.Stdout.given
import domain.RedisDocument
import io.circe.syntax._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.syntax._
import io.circe.Encoder
import io.circe.Decoder
import io.circe.parser.decode

trait RedisOperations {

  def save[A <: RedisDocument: Encoder](product: A): IO[Unit]

  def get[A <: RedisDocument: Decoder](id: String): IO[Option[A]]
}

object RedisOperations {

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  object Impl extends RedisOperations {

    val redisResource = RedisClient[IO]
      .from("redis://localhost")
      .flatMap(Redis[IO].fromClient(_, RedisCodec.Utf8))

    override def save[A <: RedisDocument: Encoder](obj: A): IO[Unit] =
      redisResource.use { redis =>
        redis.set(s"${obj.table}:${obj.id}", obj.asJson.noSpaces)
      }

    override def get[A <: RedisDocument: Decoder](
        id: String
    ): IO[Option[A]] =
      redisResource.use { redis =>
        for {
          strValue <- redis.get(id).flatTap(_ => info"Getting $id")
        } yield strValue.flatMap(decode[A](_).toOption)
      }

    def deleteById(
        id: String
    ): IO[Unit] =
      redisResource.use { redis =>
        redis.del(id).void.flatTap(_ => info"Deleted $id")
      }

  }

}
