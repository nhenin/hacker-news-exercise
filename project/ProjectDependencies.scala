import sbt._
import scala.collection.immutable

trait LibraryDependencies {

  object V {
    val akka = "2.4.17"
    val `akka-http` = "10.0.4"
    val `spray-json` = "1.3.3"
    val scalacheck = "1.13.4"
    val scalatest = "3.0.1"
  }

  val `akka-actor` = "com.typesafe.akka" %% "akka-actor" % V.akka
  val `akka-testkit` = "com.typesafe.akka" %% "akka-testkit" % V.akka

  val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % V.akka
  val `akka-stream-testkit` = "com.typesafe.akka" %% "akka-stream-testkit" % V.akka

  val `akka-http-core` = "com.typesafe.akka" %% "akka-http-core" % V.`akka-http`
  val `akka-http` = "com.typesafe.akka" %% "akka-http" % V.`akka-http`
  val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % V.`akka-http`

  val scalacheck = "org.scalacheck" %% "scalacheck" % V.scalacheck
  val scalatest = "org.scalatest" %% "scalatest" % V.scalatest
  val `akka-http-spray-json` = "com.typesafe.akka" %% "akka-http-spray-json" % V.`akka-http`
  val `spray-json` = "io.spray" %% "spray-json" % V.`spray-json`
}
object LibraryDependencies extends LibraryDependencies

trait LibraryBundles {

  val akka = LibraryBundle.fromLibraries(
    LibraryDependencies.`akka-actor`,
    LibraryDependencies.`akka-stream`
  )

  val akkaJson = immutable.Seq(
    LibraryDependencies.`akka-http-spray-json`,
    LibraryDependencies.`spray-json`
  )

  val akkaHttp = akka ++ akkaJson ++ immutable.Seq(
    LibraryDependencies.`akka-http`,
    LibraryDependencies.`akka-http-core`,
    LibraryDependencies.`akka-http-spray-json`,
    LibraryDependencies.`spray-json`
  )

  val scalatest = LibraryBundle.fromLibraries(
    LibraryDependencies.scalacheck,
    LibraryDependencies.scalatest
  )

  val akkaTesting = LibraryBundle.fromLibraries(
    LibraryDependencies.`akka-testkit`,
    LibraryDependencies.`akka-stream-testkit`
  )

  val akkaHttpTesting = akkaTesting ++ immutable.Seq(
    LibraryDependencies.`akka-http-testkit`
  )

  val akkaScalatest = scalatest ++ akkaTesting

}
object LibraryBundles extends LibraryBundles


/**
  * Goal: make it easier to manipulate bundle of libraries.
  *
  * For instance, we can apply the same configuration on all the dependencies.
  *
  * */
object LibraryBundle {
  implicit def bundle2Seq(libraryBundle: LibraryBundle): Seq[ModuleID] = libraryBundle.libraries

  implicit val appendLibraries = new Append.Values[Seq[sbt.ModuleID], LibraryBundle]() {
    override def appendValues(a: Seq[sbt.ModuleID], b: LibraryBundle): Seq[sbt.ModuleID] = {
      a ++ b.libraries
    }
  }

  def fromLibraries(libraries: ModuleID*) = new LibraryBundle(libraries)
}

case class LibraryBundle(libraries: Seq[ModuleID]) {
  def %(config: Configuration): LibraryBundle = {
    val newLibs =
      if(config == IntegrationTest)
        libraries.map(_ % "it,test") // for intelliJ and ivy
      else
        libraries.map(_ % config)

    this.copy(libraries = newLibs)
  }

  def ++(libraries: immutable.Seq[ModuleID]): LibraryBundle = this ++ LibraryBundle(libraries)
  def ++(other: LibraryBundle): LibraryBundle = this.copy(libraries = libraries ++ other.libraries)
  def :+(other: ModuleID): LibraryBundle = this.copy(libraries = libraries :+ other)
}