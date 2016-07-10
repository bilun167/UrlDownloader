name := "UrlDownloader"

version := "1.0"

scalaVersion := "2.11.8"

val myProject = (project in file(".")).enablePlugins(plugins.JUnitXmlReportPlugin)

parallelExecution in Test := true

libraryDependencies ++= Seq(
	"com.google.inject" % "guice" % "3.0",
	"com.jcraft" % "jsch" % "0.1.53",
	"junit" % "junit" % "4.12" % Test
)
