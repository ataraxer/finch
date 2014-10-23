name := "finch"

version := "0.1.0"

organization := "com.ataraxer"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "io.spray" %% "spray-can" % "1.3.1",
  "io.spray" %% "spray-http" % "1.3.1",
  "io.spray" %% "spray-client" % "1.3.1",
  "io.spray" %% "spray-routing" % "1.3.1",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "commons-codec" % "commons-codec" % "1.9",
  "com.typesafe" % "config" % "1.2.1",
  "log4j" % "log4j" % "1.2.15" excludeAll (
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jmx")),
  "org.slf4j" % "slf4j-log4j12" % "1.7.5"
    exclude("org.slf4j", "slf4j-simple"))

Revolver.settings
