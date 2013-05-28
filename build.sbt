name := "Orbital"

version := "0.1.0"

scalaVersion := "2.10.1"

scalaHome := Some(file("/usr/local/scala"))

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-target:jvm-1.6",
  "-encoding", "UTF-8",
  "-feature",
  "-optimise",
  "-Yinline-warnings"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"
)
