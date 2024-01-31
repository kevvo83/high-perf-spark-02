package proj.scalalrn.part2oop

object InheritanceExercise extends App {

  abstract class MyList[A] {
    def head: A
    def tail: MyList[A]
    def isEmpty: Boolean
    def addElem(newElem: A): MyList[A]
    def toString2: String
  }

  object Empty extends MyList[Int] {
    def head: Int = null
    def tail: MyList[Int] = null
    override def isEmpty: Boolean = true
    def addElem(newElem: Int): MyList[Int] = new Cons(newElem, Empty)
    def toString2: String = ???
  }

  class Cons[Int](h: Int, t: MyList[Int]) extends MyList[Int] {
    def head: Int = h

    def tail: MyList[Int] = t

    override def isEmpty: Boolean = false

    def addElem(newElem: Int): MyList[Int] = new Cons(newElem, this)

    def toString2: String = ???
  }

  val newList: Cons[Int] = new Cons(1, Empty)
}
