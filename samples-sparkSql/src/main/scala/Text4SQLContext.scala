
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SQLContext}

class Text4SQLContext(sc: SparkContext, sqlContext: SQLContext){

  sqlContext.experimental.extraStrategies = new TextStrategies().TextStrategy :: Nil

  def sql(sqlText: String): DataFrame = {

    sqlContext.sql(sqlText)

  }

}