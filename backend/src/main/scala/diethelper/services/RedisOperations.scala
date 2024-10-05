package diethelper.services

import cats.effect.IO
import cats.effect.Sync
import cats.implicits.*
// import cats.syntax.all._
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
import cats.Applicative

trait RedisOperations[F[_]] {

  def save[A: Encoder](document: A)(implicit doc: RedisDocument[A]): F[Unit]

  def get[A: Decoder](name: String)(implicit
      doc: RedisDocument[A]
  ): F[Option[A]]

  def delete[A](name: String)(implicit doc: RedisDocument[A]): F[Long]

  def list[A](implicit doc: RedisDocument[A]): F[List[String]]
}

// TODO what is this Sync actually?? Without i cant map over F[_]
class RedisOperationsImpl[F[_]: Sync](redis: RedisCommands[F, String, String])
    extends RedisOperations[F] {

  override def save[A: Encoder](
      document: A
  )(implicit doc: RedisDocument[A]): F[Unit] =
    redis
      .set(doc.id(document), document.asJson.noSpaces)

  override def get[A: Decoder](
      name: String
  )(implicit doc: RedisDocument[A]): F[Option[A]] =
    redis.get(doc.id(name)).map {
      case None        => None
      case Some(value) => decode[A](value).toOption
    }

  override def delete[A](name: String)(implicit
      doc: RedisDocument[A]
  ): F[Long] =
    redis
      .del(doc.id(name))

  override def list[A](implicit doc: RedisDocument[A]): F[List[String]] = {
    val pattern = s"${doc.table}:*"
    val args = ScanArgs(pattern)
    for {
      scan0 <- redis.scan(args)
      res <- loopRedisScan(scan0, args, scan0.keys)
      fixed = res.map(x => x.replace(pattern.slice(0, pattern.size - 1), ""))
    } yield fixed
  }

  private def loopRedisScan(
      prevCrs: KeyScanCursor[String],
      scanArgs: ScanArgs,
      acc: List[String]
  ): F[List[String]] = for {
    curCurs <- redis.scan(prevCrs, scanArgs)
    result <-
      if (curCurs.isFinished) Sync[F].pure(acc ++ curCurs.keys)
      else loopRedisScan(curCurs, scanArgs, acc ++ curCurs.keys)
  } yield result

}