package com.juliasoft.sensors.formatter

import com.juliasoft.sensors.core.SensorResult
import com.juliasoft.sensors.formatter.ConsoleResultFormatterSpec._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class ConsoleResultFormatterSpec extends AnyFlatSpec with Matchers {

  behavior.of("ConsoleResultFormatter")

  it should "print valid result" in {
    val formatter = new ConsoleResultFormatter(nonEmptyFilesProcessed, nonEmptyData)
    val result = formatter.format()
    result shouldBe expectedNonEmptyReport
  }

  it should "print empty result" in {
    val formatter = new ConsoleResultFormatter(emptyFilesProcessed, emptyData)
    val result = formatter.format()
    result shouldBe expectedEmptyReport
  }
}

object ConsoleResultFormatterSpec {

  private val emptyFilesProcessed = 0
  private val nonEmptyFilesProcessed = 2

  private val emptyData: Map[String, SensorResult] = Map.empty
  private val nonEmptyData: Map[String, SensorResult] = Map(
    "s2" -> SensorResult(3, 0, Some(78), Some(82), Some(88)),
    "s3" -> SensorResult(0, 1, None, None, None),
    "s1" -> SensorResult(2, 1, Some(10), Some(54), Some(98)),
  )

  private val expectedNonEmptyReport =
    """Num of processed files: 2
      |Num of processed measurements: 7
      |Num of failed measurements: 2
      |
      |Sensors with highest avg humidity:
      |
      |sensor-id,min,avg,max
      |s2,78,82,88
      |s1,10,54,98
      |s3,NaN,NaN,NaN
      |""".stripMargin

  private val expectedEmptyReport =
    """Num of processed files: 0
      |Num of processed measurements: 0
      |Num of failed measurements: 0
      |
      |Sensors with highest avg humidity:
      |
      |sensor-id,min,avg,max
      |""".stripMargin
}
