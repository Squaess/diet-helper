import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / scalaVersion := Versions.projectScalaVersion

val http4s = Seq(
  "org.http4s" %% "http4s-ember-client",
  "org.http4s" %% "http4s-ember-server",
  "org.http4s" %% "http4s-dsl",
  "org.http4s" %% "http4s-circe"
).map(_ % Versions.http4sVersion)

val logging = Seq(
  "ch.qos.logback" % "logback-classic" % "1.5.3",
  "org.typelevel" %% "log4cats-slf4j" % "2.6.0" // Direct Slf4j Support - Recommended
)

val tests = Seq(
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "org.typelevel" %% "munit-cats-effect" % Versions.catsTestVersion % Test
)

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("common"))
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % Versions.circeVersion)

  )

lazy val backend = (project in file("backend"))
  .settings(
    libraryDependencies ++=  http4s ++ logging ++ tests,
    libraryDependencies += "dev.profunktor" %% "redis4cats-effects" % Versions.redis4catsVersion,
    libraryDependencies += "org.typelevel" %% "cats-effect" % Versions.catsVersion
  )
  .dependsOn(common.jvm)

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
          ModuleSplitStyle.SmallModulesFor(List("frontend"))
        )
    },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo" %%% "laminar" % Versions.laminarVersion
  )
  .dependsOn(common.js)
