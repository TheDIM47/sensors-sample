package com.juliasoft.sensors.formatter

import com.juliasoft.sensors.core.SensorResult

object ReportUtils:

  case class Totals(validMeasurements: Long = 0L, failedMeasurements: Long = 0L)

  def calcTotals(resultMap: Map[String, SensorResult]): Totals =
    resultMap.foldLeft(Totals()) { case (total, entry) =>
      total.copy(
        validMeasurements = total.validMeasurements + entry._2.validMeasurements,
        failedMeasurements = total.failedMeasurements + entry._2.failedMeasurements,
      )
    }
