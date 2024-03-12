val scala3Version = "3.4.0"
val redis4catsVersion = "1.6.0"
val http4sVersion = "0.23.26"

lazy val root = project
  .in(file("."))
  .settings(
    name := "diet-helper",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "dev.profunktor" %% "redis4cats-effects" % redis4catsVersion,
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion
    )
  )
