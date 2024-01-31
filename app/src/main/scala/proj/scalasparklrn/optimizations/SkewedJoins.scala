package proj.scalasparklrn.optimizations

import org.apache.spark.sql.SparkSession

object SkewedJoins {

  val spark = SparkSession
    .builder().master("local")
    .appName("SkewedJoins")
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", "/Users/kevin/Downloads/spark-history-server/eventLogs/")
    .getOrCreate()



}
