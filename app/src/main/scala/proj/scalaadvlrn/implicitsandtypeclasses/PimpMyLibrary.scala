package proj.scalaadvlrn.implicitsandtypeclasses

import scala.annotation.tailrec

object PimpMyLibrary extends App {

  // Objective - add an isPrime method to the Int class so that I can do - 2.isPrime to return true/false

  /*
    1. Enrich/pimp the string class - add an:
    * `asInt` function
    * `encrypt` function that does as follows: `John` to `Lqjp`
   */
  implicit class RichString(val input: String) extends AnyVal {
    def asInt: Int =
      Integer.parseInt(input)
    def encrypt: String =
      input.map((e: Char) => (e + 2).toChar)
  }

  println(s"Convert 999 String to Int as: ${"999".asInt}, check that is of type Int is: ${"999".asInt.isInstanceOf[Int]}")
  println(s"Encrypt JoHn to ${"JoHn".encrypt}")

  assert (
    "999".asInt == 999 && "999".asInt.isInstanceOf[Int],
    "asInt function returns the right value and the right type"
  )

  /*
    2. Enrich the Int class
    * `times` function:
      3.times(() => ...)
    * `*` function
      2 * List(1,2,3) = List(1,2,3,1,2,3,1,2,3) - i.e. appends the list to itself 2 times
   */

  implicit class RichInt(val input: Int) extends AnyVal {
    def times[T](f: Int => T): T = {
      @tailrec
      def _impl(acc: T, iter: Int): T =
        if (iter <= 1) acc
        else _impl(f(input), iter - 1)
      _impl(f(input), input)
    }

    def *[T](arr: List[T]): List[T] = {
      var res: List[T] = List()
      (1 to  input).foreach(_ => res = res ++ arr)
      res
    }
  }

  /*assert (
    10.times((e: Int) => e.toString) == "10" &&
      10.times((e: Int) => e.toString).isInstanceOf[String],
    "result of the times function is a string and the value is correct"
  )*/

  assert (
    2 * List(1,2,3) == List(1,2,3,1,2,3) &&
      3 * List(9,8,7,6) == List(9,8,7,6,9,8,7,6,9,8,7,6),
    "result of the * function is correct"
  )

}
