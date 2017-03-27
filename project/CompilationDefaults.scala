import sbt.Keys._
import sbt._

object CompilationDefaults {
  val buildScalaVersion = "2.12.1"

  val settings =
    Seq(
      organization := "hackernews",
      scalaVersion := buildScalaVersion,
      incOptions := incOptions.value.withNameHashing(true),
      scalacOptions ++= Seq(
        "-feature",
        "-language:postfixOps"
      )

    )
}