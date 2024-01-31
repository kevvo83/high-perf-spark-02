package proj.scalasparklrn.essentials.dataframes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Aggregations extends App {

  val spark = SparkSession
    .builder().master("local")
    .appName("Local SC")
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", "/Users/kevin/Downloads/spark-history-server/eventLogs/")
    .getOrCreate()

  val moviesDF = spark.read.option("inferSchema", "true").json("app/src/main/resources/movies.json")
  moviesDF.printSchema()

  // 1 sum up all the profits of all the movies
  moviesDF.selectExpr("sum(Worldwide_Gross) as Sum_Worldwide_Gross_Profit").show()

  // 2 Count how many distinct directors we have
  moviesDF.selectExpr("count(distinct(Director)) as count_distinct_directors").show()

  // 3 mean + STD of US Gross Revenue of all the movies
  moviesDF.select(mean(col("US_Gross")).as("Mean_US_Gross"), std(col("US_Gross")).as("Std_Dev_US_Gross")).show()

  // 4 Average IMDB Rating and average US Gross Revenue per Director
  moviesDF.groupBy(col("Director")).agg(
    avg(col("IMDB_Rating")).as("Average_IMDB_Rating"),
    avg(col("US_Gross")).as("Average_US_Gross")
  ).sort(desc_nulls_last("Average_US_Gross")).show()

}
