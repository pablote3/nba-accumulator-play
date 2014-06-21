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
    "mysql" % "mysql-connector-java" % "5.1.23",
    "com.google.inject" % "guice" % "4.0-beta",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.4.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
	// Add your own project settings here    
  )
}
