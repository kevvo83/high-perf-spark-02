package proj.scalalrn.part3funcprog

import proj.scalalrn.part2oop.Generics._

object HOFs extends App {

  def concatenator: (String, String) => String = (a: String, b: String) => a + b

  // Function that takes an int input param, returns another func that takes an int input param and returns an int
  def func1(inputElem: Int)(inputFunc: Int => Int): (Int) => Int = inputFunc

  def nTimesBetter(f: Int => Int, numberOfTimes: Int): Int => Int =
    if (numberOfTimes <= 0) (x:Int) => x
    else (x: Int) => nTimesBetter(f, numberOfTimes - 1)(f(x))

  println(nTimesBetter((x: Int) => x + 1, 2)(10))

  trait MyList2[+A] extends MyList[A] {
    def foreach(f: A => Unit): Unit
    def sort(f: (A, A) => Int): MyList[A]
  }

  class Cons2[+A](head: A, tail: MyList2[A]) extends Cons(head, tail) with MyList2[A] {
    def foreach(f: A => Unit): Unit =
      if (isEmpty) f(head)
      else tail.foreach(f)
    def sort(f: (A, A) => Int): MyList[A] = ???
  }

  def nTimes(f: Int => Int, numberOfTimesToIncrement: Int, num: Int): Int =
    if (numberOfTimesToIncrement <= 0) num
    else nTimes(f, numberOfTimesToIncrement - 1, f(num))

  assert (
    (nTimes((x: Int) => x + 1, 2, 5) == 7) &&
      (nTimes((x: Int) => x + 5, 3, 13) == 28),
    "Values returned from nTimes are Not Accurate"
  )

  /*
    - numtimes = 0
    x => x
    - numtimes = 1
    x => f(x)
    - numtimes = 2
    x => nTimesBetter2(f, n-1)f(x)
   */

  def nTimesBetter2(f: Int => Int, numberOfTimesToIncrement: Int): Int => Int =
    if (numberOfTimesToIncrement <= 0) (x: Int) => x
    else (x: Int) => nTimesBetter2(f, numberOfTimesToIncrement - 1)(f(x))

  assert(
    nTimesBetter2((x: Int) => x + 1, 2)(10) == 12 &&
      nTimesBetter2((x: Int) => x * 4, 3)(5) == 320
  )

  // Example here - https://www.scala-exercises.org/fp_in_scala/getting_started_with_functional_programming
  def isSorted[A](arr: Array[A], ordering: (A, A) => Boolean): Boolean = {
    def impl(a: Array[A], acc: Boolean) = a match {
      case h :: t => impl(t, if (ordering(h, t.head)) acc && true else false)
      case _ => acc
    }
    impl(arr, true)
  }

  assert(
    isSorted(new Array[Int](1, 2, 3, 4, 5), (a: Int, b: Int) => if (a > b) true else false)
  )

  List(1,2 3,5)
}
