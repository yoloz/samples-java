
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


object TestTextSql {
  val conf = new SparkConf().setMaster("local").setAppName(getClass.getCanonicalName)
  val ss = SparkSession.builder().config(conf).getOrCreate()
  def main(args: Array[String]): Unit = {
    val sqlContext = ss.sqlContext
    val sparkContext = ss.sparkContext
    val ts = new Text4SQLContext(sparkContext, sqlContext)
    ts.sql(
      """create table test1(
        |word string,
        |num string
        |) using TextSource
        |options(
        |path '/home/ethan/external_sql/test1'
        |)
      """.stripMargin)
    ts.sql("INSERT INTO test1 VALUES ('zhang', 'san')")
//    ts.sql("select * from test1").show
    print("=============================================\n")
//    ts.sql(
//      """create table test2(
//        |word string,
//        |num string
//        |) using TextSource
//        |options(
//        |path '/tmp/external_sql/test2'
//        |)
//      """.stripMargin)
//    ts.sql(
//      """
//        |insert into table test2
//        |select * from test1
//      """.stripMargin)
//    ts.sql("select * from test2 order by word").show
  }
}