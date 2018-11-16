
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.sql.execution.command.{ExecutedCommandExec, RunnableCommand}

case class TextExecuteCommand(cmd: RunnableCommand) extends SparkPlan {
  override protected def doExecute(): RDD[InternalRow] = {
    ExecutedCommandExec(cmd).execute()
  }
  override def output: Seq[Attribute] = cmd.output
  override def children: Seq[SparkPlan] = Nil
}