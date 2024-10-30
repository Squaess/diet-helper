package diethelper

import pureconfig._
import pureconfig.generic.derivation.default._
import pureconfig.module.catseffect.syntax._
import cats.effect.IO

case class RedisConfig(host: String, port: Int)
case class AppConfig(redis: RedisConfig) derives ConfigReader

object AppConfig {
  def load = ConfigSource.default.loadF[IO, AppConfig]()
}
