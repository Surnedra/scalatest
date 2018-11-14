package wordcount

import java.time.{ZoneId, ZonedDateTime}

import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

class Assembly(filePath: String)(implicit spark: SparkSession) {

  import spark.implicits._

  val textDs: Dataset[String] = spark.read.textFile(filePath)

  val now: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))

  def countWord: DataFrame = {
    textDs.flatMap(line => line.split(" ")).groupByKey(identity).count()
      .toDF("word", "count")
      .withColumn("year", lit(now.getYear))
      .withColumn("month", lit(now.getMonthValue))
      .withColumn("day", lit(now.getDayOfMonth))
      .coalesce(1)
  }

  def countWordInRow: DataFrame = {
    val rowWordCount: Dataset[(String, Int)] = textDs.map(line => (line, line.split(" ").length))
    rowWordCount.toDF().select($"_1" as "line", $"_2" as "row_word_count")
      .withColumn("year", lit(now.getYear))
      .withColumn("month", lit(now.getMonthValue))
      .withColumn("day", lit(now.getDayOfMonth))
      .coalesce(1)
  }
}

