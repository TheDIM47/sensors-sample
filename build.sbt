scalaVersion := "3.2.1"
name := "sensors-sample"
organization := "com.juliasoft"
version := "0.0.1-SNAPSHOT"
description := "Sensors statistic collector sample"

scalacOptions ++= Seq(
  "-explain",
  "-explain-types",
  "-release",
  "11",
)

libraryDependencies ++= Dependencies.allLibs
