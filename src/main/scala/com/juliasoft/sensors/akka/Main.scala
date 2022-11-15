package com.juliasoft.sensors.akka

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import akka.stream.scaladsl.*
import akka.util.ByteString
import cats.implicits.*
import cats.implicits.catsSyntaxSemigroup
import com.juliasoft.sensors.core.SensorStat
import com.juliasoft.sensors.core.SensorStat.processMeasurement
import com.juliasoft.sensors.formatter.ConsoleResultFormatter
import com.juliasoft.sensors.util.FileUtils.getCsvFiles
import com.juliasoft.sensors.util.FileUtils.helpString
import com.juliasoft.sensors.util.FileUtils.parseString

import java.nio.file.Path
import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

object Main:
  private val maxFrameLength = 1024
  private val delimiter = Framing.delimiter(ByteString(System.lineSeparator()), maxFrameLength, allowTruncation = true)

  def main(args: Array[String]): Unit =
    args.headOption.fold(println(helpString())) { path =>
      getCsvFiles(path) match
        case Success(filesToRead) =>
          processWithAkkaStreams(filesToRead)
        case Failure(ex) =>
          println(s"Unable to read directory $path: ${ex.getMessage}")
    }

  private def processWithAkkaStreams(filesToRead: Seq[Path]): Unit =
    implicit val executionContext: ExecutionContext = ExecutionContext.global
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val actorMaterializer: SystemMaterializer = SystemMaterializer(actorSystem)
    (
      for {
        dataResults <- Future.traverse(filesToRead) { fileToRead =>
          FileIO
            .fromPath(fileToRead)
            .via(delimiter.map(_.utf8String).drop(1).map(parseString))
            .runFold(Map.empty[String, SensorStat]) { case (resultMap, data) =>
              val sensorId = data._1
              val sensorResult = resultMap.getOrElse(sensorId, SensorStat.empty)
              resultMap + (sensorId -> processMeasurement(data._2, sensorResult))
            }
        }
        summary = dataResults.foldLeft(Map.empty[String, SensorStat])(_ combine _)
        formatter = new ConsoleResultFormatter(filesToRead.size, summary.view.mapValues(_.toResult).toMap)
      } yield formatter.format()
    ).map(actorSystem.log.info)
      .recover { case ex: Throwable =>
        actorSystem.log.error(ex.getMessage)
      }.onComplete { _ =>
        actorMaterializer.materializer.shutdown()
        actorSystem.terminate()
      }
