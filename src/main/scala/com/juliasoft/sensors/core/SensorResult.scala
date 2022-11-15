package com.juliasoft.sensors.core

case class SensorResult(
  validMeasurements: Long,
  failedMeasurements: Long,
  min: Option[Byte],
  average: Option[Byte],
  max: Option[Byte],
)
