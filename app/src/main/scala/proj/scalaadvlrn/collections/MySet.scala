package proj.scalaadvlrn.collections

import scala.annotation.tailrec
import scala.collection.mutable

trait MySet[A] extends (A => Boolean) {

  // Contract of MySet trait - i.e. the `apply` method will only tell if the element is in the set or not
  def apply(elem: A): Boolean = contains(elem)

  def contains(x: A): Boolean
  def +(x: A): MySet[A]
  def ++(x: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatmap[B](f: A => MySet[B]): MySet[B]
  def filter(p: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  def remove(f: A => Boolean): MySet[A]
  def intersection(x: MySet[A]): MySet[A]
  def difference(x: MySet[A]): MySet[A]

  def doesNotContain(x: A): Boolean
  def unary_! : MySet[A]
}

object MySet {
  def apply[A](elems: A*): MySet[A] = {
    @tailrec
    def _impl(acc: MySet[A], e: Seq[A]): MySet[A] =
      if (e.isEmpty) acc
      else _impl(acc + e.head, e.tail)

    _impl(new Empty, elems.reverse)
  }
}

class Empty[A] extends MySet[A] {

  override def contains(x: A): Boolean = false
  def doesNotContain(x: A): Boolean = !contains(x)

  override def +(x: A): MySet[A] = new NonEmpty[A](x, this)
  override def ++(x: MySet[A]): MySet[A] = x

  override def map[B](f: A => B): MySet[B] = new Empty[B]
  override def flatmap[B](f: A => MySet[B]): MySet[B] = new Empty[B]
  override def filter(p: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = {}

  override def remove(f: A => Boolean): MySet[A] = this
  override def intersection(x: MySet[A]): MySet[A] = x
  override def difference(x: MySet[A]): MySet[A] = x

  override def unary_! : MySet[A] = new PropertyBasedSet[A]((_: A) => true)
}

class NonEmpty[A](val head: A, val tail: MySet[A]) extends MySet[A] {

  override def contains(x: A): Boolean =
    (head == x) || tail.contains(x)

  def doesNotContain(x: A): Boolean = !contains(x)

  override def +(x: A): MySet[A] = {
    if (contains(x)) this else new NonEmpty(x, this)
  }

  override def ++(x: MySet[A]): MySet[A] = tail ++ x + head

  override def map[B](f: A => B): MySet[B] =
    new NonEmpty[B](f(head), tail.map(f))

  override def flatmap[B](f: A => MySet[B]): MySet[B] =
    f(head) ++ tail.flatmap(f)

  override def filter(p: A => Boolean): MySet[A] =
    if (p(head)) tail.filter(p) + head // alternative: new NonEmpty[A](head, tail.filter(p))
    else tail.filter(p)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  // almost an inverse of filter()
  def remove(f: A => Boolean): MySet[A] =
    if (f(head)) tail.remove(f)
    else tail.remove(f) + head

  def intersection(x: MySet[A]): MySet[A] = {
    // this.filter(x.contains) - this works, but an alternative implementation is below
    /*
      The below implementation works because of the following reasons:
      1. The trait signature and type - which is MySet[A] extends (A => Boolean)
      2. x in the below call evaluates to: x.apply
        * The filter function will evaluate x.apply(elem) - which is defined as x.contains(elem)
     */
    this.filter(x)
  }

  def unary_! : MySet[A] = new PropertyBasedSet[A]((x: A) => !contains(x))

  def difference(x: MySet[A]): MySet[A] =
    new PropertyBasedSet[A]((elem: A) => contains(elem) && !x(elem))
}


/*
  This is a PropertyBasedSet - which is a nearly infinite set
 */
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {

  override def contains(x: A): Boolean = property(x)

  override def +(x: A): MySet[A] = new PropertyBasedSet[A]((elem: A) => property(elem) || elem == x)

  override def ++(x: MySet[A]): MySet[A] =
    // new PropertyBasedSet[A]((elem: A) => property(elem) || x.contains(elem))
    new PropertyBasedSet[A]((elem: A) => property(elem) || x(elem)) // alternative solution

  override def map[B](f: A => B): MySet[B] = throw new Exception("FailAsImplementationNotPossible")
  override def flatmap[B](f: A => MySet[B]): MySet[B] = throw new Exception("FailAsImplementationNotPossible")
  override def foreach(f: A => Unit): Unit = throw new Exception("FailAsImplementationNotPossible")

  override def filter(p: A => Boolean): MySet[A] = new PropertyBasedSet[A](elem => property(elem) && p(elem))
  override def remove(p: A => Boolean): MySet[A] =
    // new PropertyBasedSet[A](elem => property(elem) && !p(elem))
    filter(x => !p(x))

  override def intersection(x: MySet[A]): MySet[A] = {
    filter(x)
  }

  override def difference(x: MySet[A]): MySet[A] =
    // new PropertyBasedSet[A]((elem: A) => property(elem) && !x.contains(elem))
    filter(!x)

  override def doesNotContain(x: A): Boolean =
    !property(x)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](a => !property(a))
}

object TestMySet extends App {
  val set1 = MySet[Int](1,2,3,4,5)
  println("set1 test")
  set1 foreach (println(_))

  val set2 = (set1 + -10) ++ MySet(5,10, 20, 30, 40, 50)
  println("set2 test")
  set2 foreach (println(_))

  // Test that elements are going in in the right order - can test map() and flatMap() the same way
  var q: mutable.Queue[Int] = mutable.Queue[Int]()
  set2.foreach(q.enqueue(_))
  println(q)

  assert(
    q.dequeue() == -10 && q.dequeue() == 1 && q.dequeue() == 2 &&
      q.dequeue() == 3 && q.dequeue() == 4 && q.dequeue() == 5 && q.dequeue() == 10,
    "Elements in MySet are not in the right order"
  )

  // Test that there are no duplicated elements & that the filter function works
  var checker=0
  set2.filter(_ == 5).foreach {
    x => if (x == 5) checker += 1
  }
  assert(
    checker == 1,
    "Element 5 appears more than once in the test set named set2"
  )

  // Test insersection
  val intx: MySet[Int] = set2 intersection set1
  q = mutable.Queue[Int]()
  intx.foreach(q.enqueue(_))
  println(q)

  assert(
    q.length == 5 && q.dequeue() == 1 && q.dequeue() == 2 && q.dequeue() == 3 &&
      q.dequeue() == 4 && q.dequeue() == 5,
    "Elements in the intersection set should be 5 in total and should be (1,2,3,4,5)"
  )

  // Test unary ! function
  val negativeOfSet1 = !set1
  assert(
    negativeOfSet1(10) && negativeOfSet1(100) && !negativeOfSet1(2)
  )

  // Test difference
  val diff: MySet[Int] = set2 difference set1

  assert(
    diff.contains(-10) && diff.contains(50) &&
      !diff.contains(1) && !diff.contains(2) && !diff.contains(3) &&
      !diff.contains(4) && !diff.contains(5),
    "Assertion of the difference set failed"
  )

}
