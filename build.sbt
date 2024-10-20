val scala3Version = "3.4.0"
val redis4catsVersion = "1.6.0"
val catsVersion = "3.5.4"
val catsTestVersion = "2.0.0-M4"
val http4sVersion = "1.0.0-M40"
val circeVersion = "0.14.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "diet-helper",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % catsTestVersion % Test,
    libraryDependencies += "dev.profunktor" %% "redis4cats-effects" % redis4catsVersion,
    libraryDependencies += "org.typelevel" %% "cats-effect" % catsVersion,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.3",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0" // Direct Slf4j Support - Recommended
    ),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )
