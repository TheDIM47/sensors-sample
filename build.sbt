scalaVersion := "2.13.10"
name := "sensors-sample"
organization := "com.juliasoft"
version := "0.0.1-SNAPSHOT"
description := "Sensors statistic collector sample"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-release",
  "11",
)

libraryDependencies ++= Dependencies.allLibs
