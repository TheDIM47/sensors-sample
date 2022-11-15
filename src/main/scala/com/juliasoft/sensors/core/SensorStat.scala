package com.juliasoft.sensors.core

import cats.Semigroup
import cats.implicits.*

case class SensorStat(
  validMeasurements: Long,
  failedMeasurements: Long,
  min: Option[Byte],
  max: Option[Byte],
  sum: Option[Long],
)

object SensorStat:

  val empty: SensorStat = SensorStat(0, 0, None, None, None)

  def processMeasurement(
    maybeHumidity: Option[Byte],
    prev: SensorStat,
  ): SensorStat =
    maybeHumidity match
      case Some(humidity) =>
        prev.copy(
          validMeasurements = prev.validMeasurements + 1,
          min = Some(Math.min(humidity, prev.min.getOrElse(humidity)).toByte),
          max = Some(Math.max(humidity, prev.max.getOrElse(humidity)).toByte),
          sum = prev.sum.map(_ + humidity).orElse(Some(humidity)),
        )
      case _ =>
        prev.copy(
          failedMeasurements = prev.failedMeasurements + 1,
        )

  given Semigroup[SensorStat] with
    def combine(x: SensorStat, y: SensorStat): SensorStat =
      SensorStat(
        validMeasurements = x.validMeasurements + y.validMeasurements,
        failedMeasurements = x.failedMeasurements + y.failedMeasurements,
        min = (x.min, y.min).minimum,
        max = (x.max, y.max).maximum,
        sum = x.sum.combine(y.sum),
      )

  extension (stat: SensorStat)
    def toResult: SensorResult =
      SensorResult(
        validMeasurements = stat.validMeasurements,
        failedMeasurements = stat.failedMeasurements,
        min = stat.min,
        max = stat.max,
        average = stat.sum.map(value => (value / stat.validMeasurements).toByte),
      )
