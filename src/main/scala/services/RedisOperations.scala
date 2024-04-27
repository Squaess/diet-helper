package services

import cats.effect.IO
import cats.effect.kernel.Sync
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log.Stdout.given
import dev.profunktor.redis4cats.effects.ScanArgs
import domain.RedisDocument
import io.circe.Decoder
import io.circe.Encoder
import io.circe.parser.decode
import io.circe.syntax._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.syntax._
import dev.profunktor.redis4cats.data.KeyScanCursor
import dev.profunktor.redis4cats.RedisCommands

trait RedisOperations {

  def save[A <: RedisDocument: Encoder](document: A): IO[Unit]

  def get[A <: RedisDocument: Decoder](id: String): IO[Option[A]]

  def delete(id: String): IO[Long]

  def list(pattern: String): IO[List[String]]
}

object RedisOperations {

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  object Impl extends RedisOperations {

    val redisResource = RedisClient[IO]
      .from("redis://localhost")
      .flatMap(Redis[IO].fromClient(_, RedisCodec.Utf8))

    override def save[A <: RedisDocument: Encoder](obj: A): IO[Unit] =
      redisResource.use { redis =>
        redis
          .set(obj.id, obj.asJson.noSpaces)
          .flatTap(_ => info"saved ${obj.id}")
      }

    override def get[A <: RedisDocument: Decoder](id: String): IO[Option[A]] =
      redisResource.use { redis =>
        for {
          strValue <- redis.get(id).flatTap(_ => info"Getting $id")
        } yield strValue.flatMap(decode[A](_).toOption)
      }

    override def delete(id: String): IO[Long] =
      redisResource.use { redis =>
        redis
          .del(id)
          .flatTap(_ => info"Deleted $id")
      }

    override def list(pattern: String): IO[List[String]] = {
      val args = ScanArgs(pattern)
      redisResource
        .use { redis =>
          for {
            scan0 <- redis
              .scan(args)
            res <- loopRedisScan(redis, scan0, args, scan0.keys)
          } yield res
        }
        .map { keys =>
          keys.map(x => x.replace(pattern.slice(0, pattern.size - 1), ""))
        }
    }

    def loopRedisScan(
        redis: RedisCommands[IO, String, String],
        prevCrs: KeyScanCursor[String],
        scanArgs: ScanArgs,
        acc: List[String]
    ): IO[List[String]] =
      for {
        curCurs <- redis.scan(prevCrs, scanArgs)
        result <-
          if (curCurs.isFinished) IO.pure(acc ++ curCurs.keys)
          else loopRedisScan(redis, curCurs, scanArgs, acc ++ curCurs.keys)
      } yield result
  }

}
