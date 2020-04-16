
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.{Attribute, UnsafeProjection}
import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.unsafe.types.UTF8String

case class PhysicalText(output: Seq[Attribute], path: String) extends SparkPlan {

  override protected def doExecute(): RDD[InternalRow] = {

    sparkContext.textFile(path).map { row =>

      val fields = row.split(",").map(UTF8String.fromString)

      UnsafeProjection.create(schema)(InternalRow.fromSeq(fields))

    }

  }

  override def children: Seq[SparkPlan] = Nil

}