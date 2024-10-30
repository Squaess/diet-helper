package diethelper

import cats.effect.Async
import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout._

object RedisClientSetup {

  def createRedisResource[F[_]: Async](redisConfig: RedisConfig) = {
    val uri = s"redis://${redisConfig.host}:${redisConfig.port}"
    RedisClient[F]
      .from(uri)
      .flatMap(Redis[F].fromClient(_, RedisCodec.Utf8))
  }
}
