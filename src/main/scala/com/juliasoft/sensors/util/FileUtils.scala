package com.juliasoft.sensors.util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.jdk.CollectionConverters._
import scala.util.Try

object FileUtils {

  def getCsvFiles(dir: String): Try[Seq[Path]] = Try {
    Files.list(Paths.get(dir))
      .iterator()
      .asScala
      .filter(_.toString.toLowerCase.endsWith(".csv"))
      .toList
  }

  def parseString(string: String): (String, Option[Int]) = {
    val result = string.split(",")
    (result(0), Try(result(1).toInt).toOption)
  }

  def helpString(): String = "Usage: sbt \"run <dir with csv files>\""
}
