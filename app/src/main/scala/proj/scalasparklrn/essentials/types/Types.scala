package proj.scalasparklrn.essentials.types

import org.apache.spark.sql.{Column, DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object Types extends App {

  val spark = SparkSession
    .builder().master("local")
    .appName("Local SC")
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", "/Users/kevin/Downloads/spark-history-server/eventLogs/")
    .getOrCreate()

  val carsDF: DataFrame = spark.read.option("inferSchema", "true").json("app/src/main/resources/cars.json")

  // Types

  //  1. Filter a column based on a List[String]
  private def getListOfCars: List[String] = List("Volkswagen", "Mercedes")

  val getListOfCarsFilter: List[Column] = getListOfCars.
                                            map(elem => elem.toLowerCase()).
                                            map(elem => col("Name").contains(elem))
  val bigFilter: Column = getListOfCarsFilter.fold(lit(false))((colA: Column, colB: Column) => colA.or(colB))

  val carsFilteredDF = carsDF.filter(
    bigFilter
  )
  carsFilteredDF.explain()
  carsFilteredDF.show()

  // // Would it be faster if I repartitioned and ran the filter on each partition?
  val carsPartitionedDF = carsDF.repartition(10, col("Name"))
  carsPartitionedDF.filter(bigFilter).explain()
  println(carsPartitionedDF.filter(bigFilter).count())



  // Complex Data Types

  // 1. Filter a date column which has non-standard data formats within the dataset itself


}
