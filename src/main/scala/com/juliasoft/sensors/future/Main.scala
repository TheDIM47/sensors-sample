package com.juliasoft.sensors.future

import cats.implicits._
import cats.implicits.catsSyntaxSemigroup
import com.juliasoft.sensors.core.SensorStat
import com.juliasoft.sensors.formatter.ConsoleResultFormatter
import com.juliasoft.sensors.processor.SimpleDataProcessor
import com.juliasoft.sensors.util.FileUtils.getCsvFiles
import com.juliasoft.sensors.util.FileUtils.helpString

import java.nio.file.Path
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.io.Source
import scala.util.Failure
import scala.util.Success
import scala.util.Using

object Main {

  def main(args: Array[String]): Unit =
    if (args.length == 0) println(helpString())
    else {
      getCsvFiles(args(0)) match {
        case Success(filesToRead) =>
          val executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())
          implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executor)
          processWithFuture(filesToRead)
            .map(println)
            .foreach(_ => executor.shutdown())
        case Failure(ex) =>
          println(s"Unable to read directory ${args(0)}: ${ex.getMessage}")
      }
    }

  private def processWithFuture(filesToRead: Seq[Path])(implicit ec: ExecutionContext): Future[String] =
    for {
      dataResults <- Future.traverse(filesToRead) { path =>
        Future {
          Using(Source.fromFile(path.toFile)) { source =>
            val processor = new SimpleDataProcessor
            processor.processData(source.getLines(), Map.empty[String, SensorStat])
          }.recover { case ex =>
            println(s"Unable to read file $path: ${ex.getMessage}. File skipped")
            Map.empty[String, SensorStat]
          }.getOrElse(Map.empty[String, SensorStat])
        }
      }
      summary <- Future.successful(dataResults.foldLeft(Map.empty[String, SensorStat])(_ combine _))
      formatter = new ConsoleResultFormatter(filesToRead.size, summary.view.mapValues(_.toResult).toMap)
    } yield formatter.format()
}
