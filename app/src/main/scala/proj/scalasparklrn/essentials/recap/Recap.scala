package proj.scalasparklrn.essentials.recap

object Recap extends App {
  // https://www.baeldung.com/scala/for-comprehension
  case class Result[A](result: A) {
    def foreach(f: A => Unit): Unit = f(result)

    def map[B](f: A => B): Result[B] = Result(f(result))
  }

  val result: Result[Int] = Result(42)
  val x = for {
    res <- result
  } yield res
  println(s"Result val is ${x}")

  case class DataPoint[A, B](description: A, valx: B) {
    def map[C, D](f: (A, B) => (C, D)): DataPoint[C, D] = {
      val t: (C, D) = f(description, valx)
      DataPoint(t._1, t._2)
    }

    def foreach(f: (A, B) => Unit): Unit = f(description, valx)
  }

  val datapointA: DataPoint[String, Int] = DataPoint("Key1", 1)

  val y = datapointA.map((a, b) => ("a" + "modified", b + 10))
  y.foreach((desc, valx) => println(s"The value of ${desc} is ${valx}"))
}
