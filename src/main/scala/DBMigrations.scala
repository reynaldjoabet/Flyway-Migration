import scala.jdk.CollectionConverters._
import cats.effect.*
import cats.effect.syntax.*
import cats.implicits.*
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.Location
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

object DBMigrations {



  private def unsafeMigrate[F[_]: Sync](config: JdbcDatabaseConfig): Int = {
    val m: FluentConfiguration = Flyway
      .configure
      .dataSource(
        config.url,
        config.user,
        config.password
      )
      .group(true)//Whether to group all pending migrations together in the same transaction when applying them (only recommended for databases with support for DDL transactions).
      .outOfOrder(false)
      .table(config.migrationsTable)
      .locations(
        config.migrationsLocations.map(new Location(_)).toList: _*
      )
      .loggers("slf4j")
      .baselineOnMigrate(true)

    logValidationErrorsIfAny(m)
    m.ignoreMigrationPatterns("*:pending").load().migrate().migrationsExecuted
  }

  def migrate[F[_]: Sync](config: JdbcDatabaseConfig): F[Int] = for {
    logger <- Slf4jLogger.create[F]
    _ <- logger.info(
           "Running migrations from locations: " +
             config.migrationsLocations.mkString(", ")
         )
    count = unsafeMigrate(config)
    _ <- Sync[F].defer {

           logger.info(s"Executed $count migrations")
         }

  } yield count

  private def logValidationErrorsIfAny[F[_]: Sync](m: FluentConfiguration): F[Unit] = Slf4jLogger
    .create[F]
    .flatMap { logger =>
      val validated = m.load().validateWithResult()

      val logs = validated
        .invalidMigrations
        .asScala
        .toList
        .traverse_ { error =>
          logger.warn(s"""
                         |Failed validation:
                         |  - version: ${error.version}
                         |  - path: ${error.filepath}
                         |  - description: ${error.description}
                         |  - errorCode: ${error.errorDetails.errorCode}
                         |  - errorMessage: ${error.errorDetails.errorMessage}
        """.stripMargin.strip)
        }

      // if (!validated.validationSuccessful)
   validated.validationSuccessful.pure[F].ifM(logs, Sync[F].unit)
    }

}
