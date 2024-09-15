import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / scalaVersion := "3.5.0"

lazy val common = project
  .in(file("./common"))

lazy val frontend = project
  .in(file("./frontend"))
  .settings(
    publish := {},
    publishLocal := {},
  )
  .dependsOn(common)