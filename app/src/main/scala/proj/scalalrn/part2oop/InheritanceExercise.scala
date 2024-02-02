package proj.scalalrn.part2oop

import scala.annotation.tailrec

object InheritanceExercise extends App {

  abstract class MyList[A] {
    def head: A
    def tail: MyList[A]
    def isEmpty: Boolean
    def addElem(newElem: A): MyList[A]
    def toString2: String
    def toString3: String
  }

  object Empty extends MyList[Int] {
    def head: Int = throw new NoSuchElementException("No element in Empty List")
    def tail: MyList[Int] = null
    override def isEmpty: Boolean = true
    def addElem(newElem: Int): Cons[Int] = new Cons(newElem, this)
    def toString2: String = ""
    def toString3: String = "MyList(null, null)"
  }

  class Cons[Int](h: Int, t: MyList[Int]) extends MyList[Int] {
    def head: Int = h

    def tail: MyList[Int] = t

    override def isEmpty: Boolean = false

    def addElem(newElem: Int): Cons[Int] = new Cons(newElem, this)

    def toString2: String = if (t.isEmpty) h + t.toString2 else s"$h, " + t.toString2

    @tailrec
    private def impl(acc: String, t: MyList[Int]): String =
      if (t.isEmpty) acc + ", " + t.toString3
      else impl(acc + s", MyList(${t.head}, ${t.tail})", t.tail)

    def toString3: String = impl(s"MyList($h, $t)", t)
  }

  val newList: Cons[Int] = new Cons(1, Empty)
  assert(
    newList.head == 1,
    "Head of the new list doesn't resolve to 1"
  )

  assert(
    newList.tail match {
      case Empty => true
      case _ => false
    },
    "Tail of the new list is not of type Empty"
  )

  assert(
    !newList.isEmpty,
    "New List is empty"
  )

  val newerList = newList.addElem(99)

  assert(
    newerList.head == 99,
    "Head of the newer list doesn't resolve to 99"
  )

  assert(
    newerList.tail == newList,
    "Tail of the newer list doesn't resolve to the new (aka older) list"
  )

  assert(
    newerList.tail.tail == Empty,
    "Tail of the new list is not the Empty List"
  )

  assert(
    newerList.tail match {
      case Empty => false
      case a: Cons[Int] => true
    },
    "Tail of the newer list is not of type Cons"
  )

  val newestList = newerList.addElem(199)

  assert(
    newestList.head == 199,
    "Head of the newest list doesn't resolve to 199"
  )

  assert(
    newestList.tail == newerList,
    "Tail of the newest list should be the newer list"
  )

  assert(
    newestList.tail != newList,
    "Tail of the newest list should not be the newlist - it should be the newer list"
  )

  println(Empty.toString2)
  println(newList.toString2)
  println(newerList.toString2)

  println(Empty.toString3)
  println(newList.toString3)
  println(newerList.toString3)
  println(newestList.toString3)
}
