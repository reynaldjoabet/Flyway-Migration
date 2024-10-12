ThisBuild / scalaVersion := "3.3.3"

name := "FlywayMigration"

version := "1.0"

ThisBuild / scalacOptions ++= Seq("-no-indent")

lazy val runMigrate = taskKey[Unit]("Migrates the database schema.")
addCommandAlias("run-db-migrations", "runMigrate")

lazy val root = (project in file(".")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "3.5.4",
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
    "org.flywaydb"           % "flyway-core"     % "10.18.1",
    "com.github.pureconfig" %% "pureconfig-core" % "0.17.7",
    // https://mvnrepository.com/artifact/mysql/mysql-connector-java
    "mysql"          % "mysql-connector-java" % "8.0.33",
    "org.typelevel" %% "log4cats-slf4j"       % "2.7.0",

    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    "ch.qos.logback" % "logback-classic" % "1.5.8", //% Test
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-mysql
     "org.flywaydb" % "flyway-mysql" % "10.18.1"


  ),
  fullRunTask(runMigrate, Compile, "DBMigrationsCommand"),
  fork / runMigrate := true,
Compile / run / fork := true,
// Recommended
 Test/fork := true
)

// sbt command-line shortcut
addCommandAlias("ci-integration", "Integration/testOnly -- -n integrationTest")