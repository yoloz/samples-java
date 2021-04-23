
import java.io.File
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.sql.sources._
import org.apache.spark.sql.types.StructType



case class TextRelation(sqlContext: SQLContext, schema: StructType, path: String) extends BaseRelation with InsertableRelation {

  override def insert(data: DataFrame, overwrite: Boolean): Unit = {

    if (!new File(path).exists())

      data.rdd.map(_.mkString(",")).saveAsTextFile(path)

  }

}