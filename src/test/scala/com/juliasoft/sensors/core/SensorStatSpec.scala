package com.juliasoft.sensors.core

import com.juliasoft.sensors.core.SensorStatSpec.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.Seq

final class SensorStatSpec extends AnyFlatSpec with Matchers:

  behavior.of("SensorStat")

  it should "handle empty stat data" in {
    SensorStat.empty.toResult shouldBe expectedEmptyResult
  }

  it should "handle invalid stat data" in {
    val stat = processMeasurements(sensorThreeData)
    stat.toResult shouldBe expectedSensorThreeResult
  }

  it should "handle completely stat data" in {
    val stat = processMeasurements(sensorTwoData)
    stat.toResult shouldBe expectedSensorTwoResult
  }

  it should "handle ordinary stat data" in {
    val stat = processMeasurements(sensorOneData)
    stat.toResult shouldBe expectedSensorOneResult
  }

object SensorStatSpec:

  private val sensorOneData = Seq[Option[Byte]](Some(98), Some(10), None)
  private val sensorTwoData = Seq[Option[Byte]](Some(80), Some(78), Some(88))
  private val sensorThreeData = Seq[Option[Byte]](None)

  private val expectedSensorOneResult = SensorResult(2, 1, Some(10), Some(54), Some(98))
  private val expectedSensorTwoResult = SensorResult(3, 0, Some(78), Some(82), Some(88))
  private val expectedSensorThreeResult = SensorResult(0, 1, None, None, None)

  private val expectedEmptyResult = SensorResult(0, 0, None, None, None)

  private def processMeasurements(measurements: Seq[Option[Byte]]): SensorStat =
    measurements.foldLeft(SensorStat.empty) { (stat, maybeHumidity) =>
      SensorStat.processMeasurement(maybeHumidity, stat)
    }
