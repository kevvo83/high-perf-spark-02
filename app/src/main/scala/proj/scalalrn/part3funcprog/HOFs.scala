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

  def toCurry(f: (Int, Int) => Int): (Int => Int => Int) =
    (x: Int) => (y: Int) => f(x, y)

  def fromCurry(f: (Int => Int => Int)): (Int, Int) => Int =
    (a: Int, b: Int) => f(a)(b)

  def superAdder = toCurry(_ + _)
  assert(
    superAdder(10)(2) == 12,
    "SuperAdder(10)(2) Result should be 12"
  )

  def compose(f: Int => Int, g: Int => Int): Int => Int =
    (elem: Int) => f(g(elem))

  assert(
    compose(_ * 10, _ * 2)(13) == 260,
    "Compose(_*10, _*2)(13) result should be 260"
  )

  assert(
    andThen(_ * 10, _ + 2)(13) == 132,
    "Compose(_*10, _*2)(13) result should be 132"
  )


  def andThen(f: Int => Int, g: Int => Int): Int => Int =
    (x: Int) => g(f(x))

  def compose2[A, B, C](f: B => C, g: A => B): A => C =
    (input: A) => f(g(input))
}
