package com.juliasoft.sensors.core

case class SensorResult(
  validMeasurements: Long,
  failedMeasurements: Long,
  min: Option[Int],
  average: Option[Int],
  max: Option[Int],
)
