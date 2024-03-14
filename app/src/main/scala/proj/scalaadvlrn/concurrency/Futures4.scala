package proj.scalaadvlrn.concurrency

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success, Try}

object Futures4 extends App {

  // Question 1. Return a future immediately with a value
  Future(100) // The value is already computed, so the Future will return Immediately

  // Question 2. Write a function named inSequence(futureA, futureB) that waits for FutureA and
  def inSequence[U, T](futureA: Future[U], futureB: Future[T]): Future[T] = {
    futureA.flatMap(first => futureB)
  }

  Thread.sleep(2000)
  println(inSequence(Future(10), Future(100)))

  // Question 3. New Future with the value of the future that completes first
  def first[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    val p: Promise[A] = Promise()

    def usefulUtil(res: Try[A]): Unit =
      res match {
        case Success(a) => try{
          p.success(a)
        } catch {
          case e: Exception =>
        }

        case Failure(t) => try{
          p.failure(t)
        } catch {
          case e: Exception =>
        }
      }


    futureA.onComplete((tryAble: Try[A])=> usefulUtil(tryAble))
    futureB.onComplete((tryAble: Try[A])=> usefulUtil(tryAble))

    p.future
  }

  def first2[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    val p: Promise[A] = Promise()

    // TODO: Note the use of Promise as a container to store the result of the completed Future
    futureA.onComplete((x: Try[A]) => p.tryComplete(x)) // NOTE - the tryComplete method implements all of the try/catch logic above
    futureB.onComplete((x: Try[A]) => p.tryComplete(x)) // NOTE - the tryComplete method implements all of the try/catch logic above

    p.future
  }

  println("Question 3 working")
  val x = Await.ready(first(Future(Thread.sleep(2000)), Future(Thread.sleep(1000))), 10.seconds)
  println(x)


  // Question 4. Store the last value of the 2 futures of interest
  def last[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    val pFirst: Promise[A] = Promise()
    val pSecond: Promise[A] = Promise()

    def utilityFunc(x: Try[A]): Unit =
      x match {
        case Success(a) => try{
          pFirst.success(a)
        } catch {
          case e: Exception => pSecond.success(a)
        }
        case Failure(t) => try {
          pFirst.failure(t)
        } catch {
          case e: Exception => pSecond.failure(t)
        }
      }

    // futureA.onComplete(utilityFunc)
    // futureB.onComplete(utilityFunc)

    futureA.onComplete(a => if (!pFirst.tryComplete(a)) pSecond.tryComplete(a))
    futureB.onComplete(b => if (!pFirst.tryComplete(b)) pSecond.tryComplete(b))

    pSecond.future
  }

  val res = Await.ready(
    last(
      Future{
        Thread.sleep(2000)
        "Faster process"
      },
      Future{
        Thread.sleep(5000)
        "Slower process"
      }
    ),
    15.seconds
  )

  println("Question 4 working")
  res.foreach(x => println(s"Last future resolves to $x"))
}



