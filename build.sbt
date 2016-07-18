name := "UrlDownloader"

version := "1.0"

scalaVersion := "2.11.8"

val myProject = (project in file(".")).enablePlugins(plugins.JUnitXmlReportPlugin)

parallelExecution in Test := true

libraryDependencies ++= Seq(
	"com.google.inject" % "guice" % "3.0",
	"com.jcraft" % "jsch" % "0.1.53",
	"com.google.guava" % "guava" % "19.0",
	"com.google.guava" % "guava-collections" % "r03",
	"org.apache.httpcomponents" % "httpasyncclient" % "4.1.2",
	"org.apache.httpcomponents" % "httpcore" % "4.4.5",
	"org.apache.httpcomponents" % "httpcore-nio" % "4.4.5",
	"org.apache.httpcomponents" % "httpclient" % "4.5.2",
	"ch.qos.logback" % "logback-classic" % "1.1.7",
	"junit" % "junit" % "4.12" % Test
)
