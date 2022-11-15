package com.juliasoft.sensors.processor

import com.juliasoft.sensors.core.SensorStat

trait DataProcessor:

  def processData(source: Iterator[String], resultMap: Map[String, SensorStat]): Map[String, SensorStat]
