
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan

case class LogicalText(output: Seq[Attribute], path: String) extends LogicalPlan {
  override def children: Seq[LogicalPlan] = Nil
}
