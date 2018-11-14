package wordcount

import org.apache.spark.sql.SparkSession
import com.typesafe.config.ConfigFactory

class Setup() {
  val config = ConfigFactory.load()

  val accessKey = config.getString("cos.accessKey")
  val bucketSource = config.getString("cos.bucketSource")
  val bucketDest = config.getString("cos.bucketDest")
  val endpoint = config.getString("cos.endpoint")
  val secretKey = config.getString("cos.secretKey")
  val serviceId = config.getString("cos.serviceId")

  def initSpark: SparkSession = {
    SparkSession
      .builder
      .master("local[*]")
      .appName("CosPoc")
      .getOrCreate()
  }

  def initCos(implicit spark: SparkSession): Unit = {
    val hconf = spark.sparkContext.hadoopConfiguration

    hconf.set("fs.stocator.scheme.list", "cos")
    hconf.set("fs.cos.impl", "com.ibm.stocator.fs.ObjectStoreFileSystem")
    hconf.set("fs.stocator.cos.impl", "com.ibm.stocator.fs.cos.COSAPIClient")
    hconf.set("fs.stocator.cos.scheme", "cos")

    hconf.set("fs.cos.myCos.access.key", accessKey)
    hconf.set("fs.cos.myCos.secret.key", secretKey)
    hconf.set("fs.cos.myCos.iam.service.id", serviceId)
    hconf.set("fs.cos.myCos.endpoint", endpoint)
  }
}
