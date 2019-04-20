import sbt.Keys._
import sbt._

object CompilationDefaults {
  val buildScalaVersion = "2.12.1"

  val settings =
    Seq(
      organization := "hackernews",
      scalaVersion := buildScalaVersion,
      scalacOptions ++= Seq(
        "-feature",
        "-language:postfixOps"
      )

    )
}