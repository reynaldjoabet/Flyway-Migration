import cats.effect.*
import cats.effect.{ExitCode, IO}
import cats.effect.IOApp
import cats.implicits._

import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

object DBMigrationsCommand extends IOApp {

  /**
    * Lists all JDBC data-sources, defined in `application.conf`
    */
  val dbConfigNamespaces = List(
    "jdbc"
  )

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  def run(args: List[String]): IO[ExitCode] = {
    val migrate =
      dbConfigNamespaces.traverse_ { namespace =>
        for {
          logger <- Slf4jLogger.create[IO]
          _      <- logger.info(s"Migrating database configuration: $namespace")
          cfg    <- JdbcDatabaseConfig.loadFromGlobal[IO](namespace)//.flatMap(c=>IO.println(c).as(c))
          _      <- DBMigrations.migrate[IO](cfg)
        } yield ()
      }
    migrate.as(ExitCode.Success)
  }

}
