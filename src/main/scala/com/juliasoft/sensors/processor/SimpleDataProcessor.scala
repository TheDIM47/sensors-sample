package com.juliasoft.sensors.processor
import com.juliasoft.sensors.core.SensorStat
import com.juliasoft.sensors.core.SensorStat.processMeasurement
import com.juliasoft.sensors.util.FileUtils

final class SimpleDataProcessor extends DataProcessor {

  def processData(source: Iterator[String], resultMap: Map[String, SensorStat]): Map[String, SensorStat] =
    source
      .drop(1)
      .filter(_.nonEmpty)
      .map(FileUtils.parseString)
      .foldLeft(resultMap) { case (map, data) =>
        val sensorId = data._1
        val sensorResult = map.getOrElse(sensorId, SensorStat.empty)
        map + (sensorId -> processMeasurement(data._2, sensorResult))
      }
}
