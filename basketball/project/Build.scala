import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "basketball"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.23"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
	lessEntryPoints <<= baseDirectory(customLessEntryPoints)
  )
  
  def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
    (base / "app" / "assets" / "stylesheets" * "*.less")
  )

}
