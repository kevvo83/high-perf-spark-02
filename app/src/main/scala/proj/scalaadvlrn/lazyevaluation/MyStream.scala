package proj.scalaadvlrn.lazyevaluation

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](elem: B): MyStream[B] // prepend
  def ++[B >: A](other: => MyStream[B]): MyStream[B] // concatenate 2 streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements out of this stream
  def takeAsList(n: Int): List[A] = take(n).toListImpl()

  @tailrec
  final def toListImpl[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc
    else tail.toListImpl(head :: acc)
}

object Empty extends MyStream[Nothing] {

  override def isEmpty: Boolean = true
  override def head: Nothing = throw new NoSuchElementException()
  override def tail: MyStream[Nothing] = this

  override def #::[B >: Nothing](elem: B): MyStream[B] = new Cons(elem, this)

  override def ++[B >: Nothing](other: => MyStream[B]): MyStream[B] = other
  override def foreach(f: Nothing => Unit): Unit = {}
  override def map[B](f: Nothing => B): MyStream[B] = this
  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this
}

object MyStream {
  def from[A](startElem: A)(generator: A => A): MyStream[A] =
    new Cons(startElem, MyStream.from(generator(startElem))(generator))
}

class Cons[A](val h: A, t: => MyStream[A]) extends MyStream[A] {

  override def isEmpty: Boolean = false
  override lazy val tail: MyStream[A] = t
  override val head: A = h

  override def #::[B >: A](elem: B): MyStream[B] = new Cons(elem, this)

  override def ++[B >: A](other: => MyStream[B]): MyStream[B] =
    // head #:: tail ++ other - need to check if this implementation preserves the lazy evaluation
    new Cons(head, tail ++ other)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def map[B](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f))

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): MyStream[A] =
    if (predicate(head)) new Cons(h, tail.filter(predicate))
    else tail.filter(predicate)

  override def take(n: Int): MyStream[A] = {
    if (n <= 0) Empty
    else if (n == 1) new Cons(head, Empty)
    else new Cons(head, tail.take(n-1))
  }

}

object TestMyStream extends App {
  val naturals = MyStream.from(1)(x => x + 1)

  assert(naturals.head == 1, "Head of the infinite sequence is incorrect")
  assert(naturals.tail.head == 2, "Next element in the infinite sequence is correct")

  assert(
    naturals.takeAsList(10).length == 10,
    "Length of list returned by `takeAsList` is incorrect"
  )

  naturals.take(20).foreach(println)

  assert(
    naturals.map(_ * 2).head == 2
  )
  assert(
    naturals.flatMap(x => new Cons(x, new Cons(x + 1, Empty))).take(10).tail.tail.tail.head == 3
  )

  // naturals.filter(_ < 10).take(100).foreach(println) causes a stackoverflow error
  naturals.take(100).filter(_ < 10).foreach(println)



  // Exercises
  // 1. Stream of fibonacci numbers
  println("Fibonacci exercise")
  def fib(a: Int, b: Int): MyStream[Int] = new Cons(a, fib(b, a + b))
  fib(0, 1).take(10).foreach(println)

  // 2. Stream of prime numbers - with Eratosthenes' sieve
  /*
    [2] ++ [ 3, 4, 5, 6, ...... ].
        filter(_ % 2 != 0).
        filter(_ % 3 != 0).
        filter(_ % 4 != 0).
        filter(_ ..5..
   */
  println("Prime numbers exercise")
  val naturalNumbers = MyStream.from(2)(x => x + 1)

  def primeStream(inputStream: MyStream[Int]): MyStream[Int] = {
    if (inputStream.isEmpty) inputStream
    else
      new Cons(inputStream.head, primeStream(inputStream.tail.filter(_ % inputStream.head != 0)))
  }

  primeStream(naturalNumbers).take(50).foreach(println)


  /*
  ESSENTIALLY - what you have that cannot be evaluated lazily, should be passed by name to a function -
  def f(pipe: => Stream[A]) { // passed by name here - so not evaluated
    lazy val pipe2 = pipe // together with this line, becomes call-by-need - i.e. pipe will only be evaluated once within the block when it's needed
  }
   */

}

