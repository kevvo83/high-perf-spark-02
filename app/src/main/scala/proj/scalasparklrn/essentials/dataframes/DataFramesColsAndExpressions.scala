package proj.scalasparklrn.essentials.dataframes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import scala.io.Source

object DataFramesColsAndExpressions extends App {

  val spark = SparkSession
    .builder().master("local")
    .appName("Local SC")
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", "/Users/kevin/Downloads/spark-history-server/eventLogs/")
    .getOrCreate()

  import spark.implicits._

  // Use multiple ways to do these exercises
  // 1. Read the movies DF and read 2 columns - title and release date
  val moviesDF: DataFrame = spark.read.json("app/src/main/resources/movies.json").
    select(col("Title"), col("Release_Date"))

  val movies2DF: DataFrame = spark.read.json("app/src/main/resources/movies.json").selectExpr("Title", "Release_Date")

  movies2DF.show(5)

  // 2. Sum up gross profits of all movies - US Gross, Worldwide Gross, DVD sales - into 1 column
  val moviesGpDF = spark.read.json("app/src/main/resources/movies.json").
    withColumn("Gross",
      nvl(col("US_Gross"), lit(0)) + nvl(col("Worldwide_Gross"), lit(0)) + nvl(col("US_DVD_Sales"), lit(0))
    )

  moviesGpDF.show(5)

  // 3. Select all the comedy movies ("Major_Genre" column) with IMDB_Rating > 6
  val decentComedyMoviesDF = spark.read.json("app/src/main/resources/movies.json").
    filter(col("Major_Genre") === "Comedy").filter(col("IMDB_Rating") > 6.0)

  decentComedyMoviesDF.show(5)

  val decentComedyMovies2DF = spark.read.json("app/src/main/resources/movies.json").
    where((col("Major_Genre") === "Comedy") && (col("IMDB_Rating") > 6.0))

  decentComedyMovies2DF.show(5)



}
