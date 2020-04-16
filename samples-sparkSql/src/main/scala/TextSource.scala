
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources._
import org.apache.spark.sql.types.StructType


class TextSource extends SchemaRelationProvider {

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType):
  BaseRelation = {

    val path = parameters.getOrElse("path", "/tmp/external_sql/testSql")

    TextRelation(sqlContext, schema, path)

  }

}