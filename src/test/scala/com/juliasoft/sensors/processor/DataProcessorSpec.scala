package com.juliasoft.sensors.processor

import com.juliasoft.sensors.core.SensorStat
import com.juliasoft.sensors.processor.DataProcessorSpec.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class DataProcessorSpec extends AnyFlatSpec with Matchers:

  behavior.of("DataProcessor")

  it should "process data and skip empty lines" in {
    val resultMap = Map.empty[String, SensorStat]
    val processor = new SimpleDataProcessor
    processor.processData(nonEmptyLines.iterator, resultMap) shouldBe expectedNonEmptyResult
  }

  it should "process empty data" in {
    val resultMap = Map.empty[String, SensorStat]
    val processor = new SimpleDataProcessor
    processor.processData(Seq.empty.iterator, resultMap) shouldBe Map.empty[String, SensorStat]
  }

object DataProcessorSpec:

  private val nonEmptyLines = Seq(
    "sensor-id,humidity",
    "",
    "s1,10",
    "s2,88",
    "s1,NaN",
    "",
    "s2,80",
    "s3,NaN",
    "s2,78",
    "s1,98",
  )

  private val expectedNonEmptyResult = Map[String, SensorStat](
    "s1" -> SensorStat(2, 1, Some(10), Some(98), Some(108)),
    "s2" -> SensorStat(3, 0, Some(78), Some(88), Some(246)),
    "s3" -> SensorStat(0, 1, None, None, None),
  )
