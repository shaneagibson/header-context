import sbt._
import sbt.Keys._

object LibraryBuild extends Build {

  val appName = "header-context"

  lazy val microservice = Project(appName, file("."))
    .settings(
      libraryDependencies ++= LibraryDependencies(),
      crossScalaVersions := Seq("2.11.6"),
      resolvers := Seq(
        "typesafe-releases" at "http://repo.typesafe.com/typesafe/releases/"
      )
    )
}

private object LibraryDependencies {

  import play.PlayImport._
  import play.core.PlayVersion

  val compile = Seq(
    "com.typesafe.play" %% "play" % PlayVersion.current,
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "org.slf4j" % "slf4j-simple" % "1.6.4",
    ws
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test: Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.scalatest" %% "scalatest" % "2.2.4" % scope,
        "org.scalaj" %% "scalaj-http" % "1.1.5" % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
