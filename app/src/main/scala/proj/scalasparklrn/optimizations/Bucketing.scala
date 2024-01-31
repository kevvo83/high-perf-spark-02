package proj.scalasparklrn.optimizations

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


object Bucketing extends App {

  val spark = SparkSession
    .builder().master("local")
    .appName("Bucketing")
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", "/Users/kevin/Downloads/spark-history-server/eventLogs/")
    .getOrCreate()

  val flightsDF = spark.read.option("inferSchema", "true").
    json("app/src/main/resources/flights.json").
    repartition(4)

  // Just partitioned
  val delayedFlights = flightsDF.
    filter((col("dest") === "DEN") and (col("arrdelay") > 1.0)).
    groupBy("carrier", "origin", "dest").agg(avg(col("arrdelay")))
  delayedFlights.show()
  delayedFlights.explain()

  /* Has Exchange for the Group By
    == Physical Plan ==
    AdaptiveSparkPlan isFinalPlan=false
    +- HashAggregate(keys=[carrier#10, origin#19, dest#16], functions=[avg(arrdelay#9)])
       +- Exchange hashpartitioning(carrier#10, origin#19, dest#16, 200), ENSURE_REQUIREMENTS, [plan_id=114]
          +- HashAggregate(keys=[carrier#10, origin#19, dest#16], functions=[partial_avg(arrdelay#9)])
             +- Exchange RoundRobinPartitioning(4), REPARTITION_BY_NUM, [plan_id=111]
                +- Filter (((isnotnull(dest#16) AND isnotnull(arrdelay#9)) AND (dest#16 = DEN)) AND (arrdelay#9 > 1.0))
                   +- FileScan json [arrdelay#9,carrier#10,dest#16,origin#19] Batched: false, DataFilters: [isnotnull(dest#16), isnotnull(arrdelay#9), (dest#16 = DEN), (arrdelay#9 > 1.0)], Format: JSON, Location: InMemoryFileIndex(1 paths)[file:/Users/kevin/IdeaProjects/high-perf-spark-02/app/src/main/resourc..., PartitionFilters: [], PushedFilters: [IsNotNull(dest), IsNotNull(arrdelay), EqualTo(dest,DEN), GreaterThan(arrdelay,1.0)], ReadSchema: struct<arrdelay:double,carrier:string,dest:string,origin:string>
   */

  // vs. Bucketed
  flightsDF.write.mode("overwrite").
    partitionBy("origin").
    bucketBy(4, "carrier", "dest").
    saveAsTable("flights_bucketed")

  val delayedFlights2DF = spark.table("flights_bucketed").
    filter((col("dest") === "DEN") and (col("arrdelay") > 1.0)).
    groupBy("carrier", "origin", "dest").agg(avg(col("arrdelay")).as("arrdelay")).
    orderBy(col("arrdelay").desc_nulls_last)

  delayedFlights2DF.show()
  delayedFlights2DF.explain()

  // If you refer to the Physical plan in the .explain() call, there is no exchange for the delayedFlights2DF.show()
  // The Bucketing and save to the table is an investment

}
