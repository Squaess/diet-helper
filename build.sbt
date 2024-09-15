import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.5.0"
val redis4catsVersion = "1.6.0"
val catsVersion = "3.5.4"
val catsTestVersion = "2.0.0-M4"
val http4sVersion = "1.0.0-M40"
val circeVersion = "0.14.1"

ThisBuild / scalaVersion := scala3Version

val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val http4s = Seq(
  "org.http4s" %% "http4s-ember-client",
  "org.http4s" %% "http4s-ember-server",
  "org.http4s" %% "http4s-dsl",
  "org.http4s" %% "http4s-circe"
).map(_ % http4sVersion)

val logging = Seq(
  "ch.qos.logback" % "logback-classic" % "1.5.3",
  "org.typelevel" %% "log4cats-slf4j" % "2.6.0" // Direct Slf4j Support - Recommended
)

val tests = Seq(
    "org.scalameta" %% "munit" % "0.7.29" % Test,
    "org.typelevel" %% "munit-cats-effect" % catsTestVersion % Test,
)

lazy val common = crossProject.crossType(CrossType.Pure).in(file("common"))


lazy val backend = (project in file("backend"))
  .settings(
    libraryDependencies ++= circe ++ http4s ++ logging ++ tests,
    libraryDependencies += "dev.profunktor" %% "redis4cats-effects" % redis4catsVersion,
    libraryDependencies += "org.typelevel" %% "cats-effect" % catsVersion,
  )

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    publish := {},
    publishLocal := {},
    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("frontend")))
    },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0"
  )
  .dependsOn(common.js)