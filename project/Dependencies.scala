import sbt.*

object Dependencies {

  val akkaLibs = Seq("com.typesafe.akka" %% "akka-stream" % "2.6.20")

  val circeLibs = Seq(
    "io.circe" %% "circe-core" % "0.14.3",
    "io.circe" %% "circe-generic" % "0.14.3",
  )

  val catsLibs = Seq("org.typelevel" %% "cats-core" % "2.9.0")

  val testLibs = Seq("org.scalatest" %% "scalatest" % "3.2.14" % Test)

  val allLibs: Seq[ModuleID] = akkaLibs ++ circeLibs ++ catsLibs ++ testLibs
}
