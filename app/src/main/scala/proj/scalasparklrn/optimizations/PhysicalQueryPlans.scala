package proj.scalasparklrn.optimizations

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object PhysicalQueryPlans extends App {

  val spark = SparkSession
    .builder().master("local")
    .appName("PhysicalQueryPlanAnalysis")
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", "/Users/kevin/Downloads/spark-history-server/eventLogs/")
    .getOrCreate()

  val df = spark.range(1, 1000000, 1)

  // Projection
  val dfTimes5 = df.selectExpr("id * 5 as id")
  dfTimes5.explain()

  // Repartition
  val dfRePart = df.repartition(10, col("id"))
  dfRePart.explain()

  val dfRePart2 = df.repartition(7)
  dfRePart2.explain()

  // Difference between Lambda and Expr in DataFrames' physical explain plan ??

}
