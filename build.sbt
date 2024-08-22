import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.4.0"
val redis4catsVersion = "1.6.0"
val catsVersion = "3.5.4"
val catsTestVersion = "2.0.0-M4"
val http4sVersion = "1.0.0-M40"
val circeVersion = "0.14.1"

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

lazy val backend = project
  .in(file("./app/backend"))
  .settings(
    name := "diet-helper",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= circe ++ http4s ++ logging ++ tests,
    libraryDependencies += "dev.profunktor" %% "redis4cats-effects" % redis4catsVersion,
    libraryDependencies += "org.typelevel" %% "cats-effect" % catsVersion,
  )

lazy val frontend = project
  .in(file("./app/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := scala3Version,

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "livechart" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("frontend")))
    },

    /* Depend on the scalajs-dom library.
     * It provides static types for the browser DOM APIs.
     */
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0"
  )