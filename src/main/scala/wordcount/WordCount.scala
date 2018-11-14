package wordcount

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.typesafe.config.ConfigFactory

import org.apache.log4j.{Level, LogManager}

object WordCount extends App {
  val log = LogManager.getRootLogger

  log.setLevel(Level.INFO)
  log.info("start of thing")

  val config = ConfigFactory.load()

  val setup = new Setup()
  implicit val spark: SparkSession = setup.initSpark

  setup.initCos

  val assembly = new Assembly(setup.bucketSource)
  val wordCountDf: DataFrame = assembly.countWord
  val rowWordCountDf: DataFrame = assembly.countWordInRow

  wordCountDf.write.partitionBy("year", "month", "day").save(setup.bucketDest + "/word-count")
  rowWordCountDf.write.partitionBy("year", "month", "day").save(setup.bucketDest + "/row-word-count")

  log.info("end of thing")
  spark.stop()
}