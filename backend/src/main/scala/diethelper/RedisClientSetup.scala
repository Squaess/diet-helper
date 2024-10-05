package diethelper

import cats.effect.{Resource, Async}
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout._

object RedisClientSetup {

  def createRedisResource[F[_]: Async] = {
    RedisClient[F]
      .from("redis://localhost")
      .flatMap(Redis[F].fromClient(_, RedisCodec.Utf8))
  }
}
