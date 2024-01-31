package proj.scalasparklrn.optimizations

import org.apache.spark.sql.{DataFrame, SparkSession, Dataset}
import org.apache.spark.sql.functions._

object PrePartitioning extends App {

  val spark = SparkSession
    .builder().master("local")
    .appName("PrePartitioningOpt")
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", "/Users/kevin/Downloads/spark-history-server/eventLogs/")
    .getOrCreate()

  val df1 = spark.range(1, 1000000, 1).repartition(10) // Physical plan shows that this employs RoundRobinPartitioning
  val df2 = spark.range(1, 2000000, 2).repartition(7) // Physical plan shows that this employs RoundRobinPartitioning

  private def addNewColumns[A](df: Dataset[A], i: Int): DataFrame = {
    val cols = "id" +: (1 to i).map(elem => s"id * $elem as newCol$elem")
    df.selectExpr(cols: _*)
  }

  // Manually disable the Auto Broadcast - to test the effects of repartitioning on perf
  //     Need to disable Auto Broadcast as the DFs we're using here are so small, that Spark will automatically
  //     ... broadcast the DFs - and then we can't analyze the impact of Pre-partitioning on perf
  spark.conf.set("spark.sql.autoBroadcastJoinThreshold", -1)

  // Partition + Add new cols + Join
  val newColsAddedToDF1 = addNewColumns(df1, 5) // wide(r) DF now
  val join1DF = df2.join(newColsAddedToDF1, "id")
  join1DF.explain()

  // Partition + Join + Add new cols
  val df1repart = df1.repartition(col("id"))
  val df2repart = df2.repartition(col("id"))
  val join2DF = addNewColumns(df2repart.join(df1repart, "id"), 5)
  join2DF.explain()

  join1DF.count()

  join2DF.count()

}
