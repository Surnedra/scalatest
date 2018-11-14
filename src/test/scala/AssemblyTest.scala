import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.{Row, SparkSession}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import wordcount.Assembly

class AssemblyTest extends FlatSpec
  with Matchers
  with DataFrameSuiteBase
  with BeforeAndAfter {

  var assembly: Assembly = _
  var YEAR: Int = _
  var MONTH: Int = _
  var DAY: Int = _

  before {
    implicit val ss: SparkSession = spark
    assembly = new Assembly("src/test/resources/hello_world.txt")
    YEAR = assembly.now.getYear
    MONTH = assembly.now.getMonthValue
    DAY = assembly.now.getDayOfMonth
  }

  "countWord" should "contain 3 unique words" in {
    assembly.countWord.collect should contain theSameElementsAs Seq(Row("World!",1, YEAR, MONTH, DAY), Row("Hello",2, YEAR, MONTH, DAY), Row("Spark!",1, YEAR, MONTH, DAY))
  }

  "countWordInRow" should "contain 2 rows" in {
     assembly.countWordInRow.collect should contain theSameElementsAs Seq(Row("Hello World!", 2, YEAR, MONTH, DAY), Row("Hello Spark!", 2, YEAR, MONTH, DAY))
   }
}
