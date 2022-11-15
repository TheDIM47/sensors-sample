package com.juliasoft.sensors.simple

import com.juliasoft.sensors.core.SensorStat
import com.juliasoft.sensors.formatter.ConsoleResultFormatter
import com.juliasoft.sensors.processor.SimpleDataProcessor
import com.juliasoft.sensors.util.FileUtils.getCsvFiles
import com.juliasoft.sensors.util.FileUtils.helpString

import java.nio.file.Path
import scala.collection.immutable.Seq
import scala.io.Source
import scala.util.Failure
import scala.util.Success
import scala.util.Using

object Main:

  def main(args: Array[String]): Unit =
    args.headOption.fold(println(helpString())) { path =>
      getCsvFiles(path) match
        case Success(filesToRead) =>
          println(processSimple(filesToRead))
        case Failure(ex) =>
          println(s"Unable to read directory $path: ${ex.getMessage}")
    }

  private def processSimple(filesToRead: Seq[Path]): String =
    val processor = new SimpleDataProcessor
    val results = filesToRead
      .foldLeft(Map.empty[String, SensorStat]) { case (resultMap, path) =>
        Using(Source.fromFile(path.toFile)) { source =>
          processor.processData(source.getLines(), resultMap)
        }.recover { case ex =>
          println(s"Unable to read file $path: ${ex.getMessage}. File skipped")
          Map.empty[String, SensorStat]
        }.getOrElse(Map.empty)
      }
    new ConsoleResultFormatter(filesToRead.size, results.view.mapValues(_.toResult).toMap)
      .format()
