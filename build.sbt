import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / scalaVersion := "3.5.0"

lazy val common = crossProject.crossType(CrossType.Pure).in(file("common"))

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