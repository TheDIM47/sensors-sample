package com.juliasoft.sensors.formatter

import com.juliasoft.sensors.core.SensorResult
import com.juliasoft.sensors.formatter.ConsoleResultFormatter.*
import com.juliasoft.sensors.formatter.ReportUtils.calcTotals

final class ConsoleResultFormatter(
  numOfFiles: Int,
  resultMap: Map[String, SensorResult],
) extends ResultFormatter[String]:

  def format(): String =
    val headerString = makeHeader(numOfFiles, resultMap)
    val resultsString = makeResults(resultMap)
    s"$headerString$resultsString"

object ConsoleResultFormatter:

  private def makeHeader(numOfFiles: Int, resultMap: Map[String, SensorResult]): String =
    val totals = calcTotals(resultMap)
    header(numOfFiles, totals.validMeasurements + totals.failedMeasurements, totals.failedMeasurements)

  private object DefaultReportOrdering extends Ordering[(String, SensorResult)]:

    def compare(a: (String, SensorResult), b: (String, SensorResult)): Int =
      a._2.average.getOrElse(Byte.MinValue)
        .compare(b._2.average.getOrElse(Byte.MinValue))

  private def makeResults(result: Map[String, SensorResult]): String =
    result.toList
      .sorted(DefaultReportOrdering.reverse)
      .map(entry => s"${entry._1},${showSensorResult(entry._2)}${System.lineSeparator()}")
      .mkString

  private def showOption[A](value: Option[A]): String = value.fold("NaN")(_.toString)

  private def showSensorResult(result: SensorResult): String =
    s"""${showOption(result.min)},${showOption(result.average)},${showOption(result.max)}"""

  private def header(numOfFiles: Int, validMeasurements: Long, failedMeasurements: Long): String =
    s"""Num of processed files: $numOfFiles
      |Num of processed measurements: $validMeasurements
      |Num of failed measurements: $failedMeasurements
      |
      |Sensors with highest avg humidity:
      |
      |sensor-id,min,avg,max
      |""".stripMargin
