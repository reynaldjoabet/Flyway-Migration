import cats.effect.Sync

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.{ConfigConvert, ConfigSource}
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader
//import pureconfig.generic.derivation.ConfigReaderDerivation.Default.derived

final case class JdbcDatabaseConfig(
  url: String,
  driver: String,
  user: String,
  password: String,
  migrationsTable: String,
  migrationsLocations: List[String]
) derives ConfigReader

object JdbcDatabaseConfig {

  def loadFromGlobal[F[_]: Sync](configNamespace: String): F[JdbcDatabaseConfig] =
    Sync[F].defer {
      val config = ConfigFactory.load()
      load(config.getConfig(configNamespace))
    }

  def load[F[_]: Sync](config: Config): F[JdbcDatabaseConfig] =
    Sync[F].delay {
      ConfigSource.fromConfig(config).loadOrThrow
    }

}
