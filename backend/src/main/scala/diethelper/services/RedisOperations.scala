package diethelper.services

import cats.effect.IO
import cats.effect.kernel.Sync
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log.Stdout.given
import dev.profunktor.redis4cats.effects.ScanArgs
import diethelper.domain.db.RedisDocument
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

  def save[A: Encoder](document: A)(implicit doc: RedisDocument[A]): IO[Unit]

  def get[A: Decoder](name: String)(implicit
      doc: RedisDocument[A]
  ): IO[Option[A]]

  def delete[A](name: String)(implicit doc: RedisDocument[A]): IO[Long]

  def list[A](implicit doc: RedisDocument[A]): IO[List[String]]
}

object RedisOperations {

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  object Impl extends RedisOperations {

    val redisResource = RedisClient[IO]
      .from("redis://localhost")
      .flatMap(Redis[IO].fromClient(_, RedisCodec.Utf8))

    override def save[A: Encoder](
        document: A
    )(implicit doc: RedisDocument[A]): IO[Unit] =
      redisResource.use { redis =>
        redis
          .set(doc.id(document), document.asJson.noSpaces)
      }

    override def get[A: Decoder](
        name: String
    )(implicit doc: RedisDocument[A]): IO[Option[A]] =
      redisResource.use { redis =>
        redis.get(doc.id(name)).flatTap(item => info"$item").map {
          case None        => None
          case Some(value) => decode[A](value).toOption
        }
      }

    override def delete[A](
        name: String
    )(implicit doc: RedisDocument[A]): IO[Long] =
      redisResource.use { redis =>
        redis
          .del(doc.id(name))
          .flatTap(_ => info"Deleted $name")
      }

    override def list[A](implicit doc: RedisDocument[A]): IO[List[String]] = {
      val pattern = s"${doc.table}:*"
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

    private def loopRedisScan(
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
