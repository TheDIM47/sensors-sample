package com.juliasoft.sensors.formatter

import com.juliasoft.sensors.core.SensorResult
import com.juliasoft.sensors.formatter.ReportUtils.calcTotals
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

final class JsonResultFormatter(
  numOfFiles: Int,
  resultMap: Map[String, SensorResult],
) extends ResultFormatter[Json] {

  def format(): Json = {
    val totals = calcTotals(resultMap)
    Json.obj(
      ("filesProcessed", Json.fromInt(numOfFiles)),
      ("valid", Json.fromLong(totals.validMeasurements + totals.failedMeasurements)),
      ("failed", Json.fromLong(totals.failedMeasurements)),
      ("data", resultMap.asJson),
    )
  }
}
