package com.juliasoft.sensors.core

import cats.Semigroup
import cats.implicits._

case class SensorStat(
  validMeasurements: Long,
  failedMeasurements: Long,
  min: Option[Int],
  max: Option[Int],
  sum: Option[Long],
)

object SensorStat {

  val empty: SensorStat = SensorStat(0, 0, None, None, None)

  def processMeasurement(
    maybeHumidity: Option[Int],
    prev: SensorStat,
  ): SensorStat =
    maybeHumidity match {
      case Some(humidity) =>
        prev.copy(
          validMeasurements = prev.validMeasurements + 1,
          min = Some(Math.min(humidity, prev.min.getOrElse(humidity))),
          max = Some(Math.max(humidity, prev.max.getOrElse(humidity))),
          sum = prev.sum.map(_ + humidity).orElse(Some(humidity.toLong)),
        )
      case _ =>
        prev.copy(
          failedMeasurements = prev.failedMeasurements + 1,
        )
    }

  implicit val sensorStatSemigroup: Semigroup[SensorStat] = new Semigroup[SensorStat] {

    def combine(x: SensorStat, y: SensorStat): SensorStat =
      SensorStat(
        validMeasurements = x.validMeasurements + y.validMeasurements,
        failedMeasurements = x.failedMeasurements + y.failedMeasurements,
        min = (x.min, y.min).minimum,
        max = (x.max, y.max).maximum,
        sum = x.sum.combine(y.sum),
      )
  }

  implicit final class SensorStatOps(val stat: SensorStat) extends AnyVal {

    def toResult: SensorResult =
      SensorResult(
        validMeasurements = stat.validMeasurements,
        failedMeasurements = stat.failedMeasurements,
        min = stat.min,
        max = stat.max,
        average = stat.sum.map(value => (value / stat.validMeasurements).toInt),
      )
  }
}
