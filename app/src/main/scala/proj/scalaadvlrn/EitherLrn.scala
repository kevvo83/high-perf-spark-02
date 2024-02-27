package proj.scalaadvlrn

import java.util.Random
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object EitherLrn extends App {

  val randomizer: Random = new Random()

  def getResult: Future[Either[Throwable, String]] = Future {
    try {
      if (randomizer.nextBoolean()) Right("Hello")
      else throw new Exception("")
    }
    catch {
      case e: Throwable => Left(e)
    }
  }

  val getResultSimpler1: Either[Throwable, String] =
    if (randomizer.nextBoolean()) Right("Hello1")
    else Left(new Exception("Simpler1Failure"))

  val getResultSimpler2: Either[Throwable, String] =
    if (true) Right("Hello2")
    else Left(new Exception("Simpler2Failure"))

  val res2 = for {
    x1 <- getResultSimpler1
    x2 <- getResultSimpler2
  } yield (x1, x2)
  println(s"Res2 is $res2")

  val res = getResult map {
    case Right(s) => s
    case Left(e) => e
  }

  println(Right(100).flatMap(x => Right(x + 1)))
  println(Right(100).map(x => x + 1))

}
