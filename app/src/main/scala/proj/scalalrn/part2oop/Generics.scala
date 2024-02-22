package proj.scalalrn.part2oop

import scala.annotation.tailrec

object Generics extends App {

  trait MyPredicate[-T] { // TODO: Why is the type contravariant?
    def test(elem: T): Boolean
  }

  trait MyTransformer[-A, B] { // TODO: Why is the type contravariant?
    def transform(inputElem: A): B
  }

  abstract class MyList[+A] {
    def head: A
    def tail: MyList[A]
    def isEmpty: Boolean
    def addElem[B >: A](newElem: B): MyList[B] // TODO: Why is the type B >: A ?
    def toString2: String

    def map[B](tx: MyTransformer[A, B]): MyList[B]
    def filter(fp: MyPredicate[A]): MyList[A]
    def flatMap[B](tx: MyTransformer[A, MyList[B]]): MyList[B]

    def ++[B >: A](list: MyList[B]): MyList[B] // TODO: Why is the type B >: A ?

    def foreach(f: A => Unit): Unit
    def sort(f: (A, A) => Int): MyList[A]
    def zipWith[B, C](l: MyList[B], f: (A, B) => C): MyList[C]
    def fold[B](agg: B)(f: (A, B) => B): B
  }

  object Empty extends MyList[Nothing] {
    def head: Nothing = throw new NoSuchElementException()
    def tail: MyList[Nothing] = this
    def isEmpty: Boolean = true
    def addElem[B >: Nothing](newElem: B): MyList[B] = new Cons[B](newElem, this)
    def toString2: String = ""
    def filter(f: MyPredicate[Nothing]): MyList[Nothing] = this
    def map[B](tx: MyTransformer[Nothing, B]): MyList[B] = this
    def flatMap[B](tx: MyTransformer[Nothing, MyList[B]]): MyList[B] = this
    def ++[B >: Nothing](list: MyList[B]): MyList[B] = list
    def foreach(f: Nothing => Unit): Unit = {}
    def sort(f: (Nothing, Nothing) => Int): MyList[Nothing] = this
    def zipWith[B, C](l: MyList[B], f: (Nothing, B) => C): MyList[C] = this
    def fold[B](agg: B)(f: (Nothing, B) => B): B = agg
  }

  class Cons[+A](h: A, t: MyList[A]) extends MyList[A] {
    def head: A = h
    def tail: MyList[A] = t
    def isEmpty: Boolean = false
    def addElem[B >: A](newElem: B): MyList[B] = new Cons[B](newElem, this)
    def toString2: String = if (t.isEmpty) s"$h" + t.toString2 else s"$h, " + t.toString2

    def filter2(fp: MyPredicate[A]): MyList[A] = {
      @tailrec
      def _impl(acc: MyList[A], x: MyList[A]): MyList[A] = {
        if ((!x.isEmpty) && fp.test(x.head)) _impl(acc.addElem(x.head), x.tail)
        else if (!x.isEmpty) _impl(acc, x.tail)
        else acc
      }

      _impl(Empty, this)
    }

    def filter(fp: MyPredicate[A]): MyList[A] = {
      if (fp.test(head)) new Cons(h,  t.filter(fp))
      else tail.filter(fp)
    }

    def map[B](tx: MyTransformer[A, B]) = new Cons(tx.transform(head), t.map(tx))

    def ++[B >: A](list: MyList[B]): MyList[B] = new Cons(head, tail ++ list)

    def flatMap[B](tx: MyTransformer[A, MyList[B]]): MyList[B] =
      tx.transform(head) ++ tail.flatMap(tx)

    def foreach(func: A => Unit): Unit = {
      func(h)
      tail.foreach(func)
    }

    def sort(f: (A, A) => Int): MyList[A] = ??? // TODO: Implement the stupid fucking sort function

    def zipWith[B, C](l: MyList[B], f: (A, B) => C): MyList[C] =
      new Cons(f(head, l.head), tail.zipWith(l.tail, f))

    def fold[B](agg: B)(f: (A, B) => B): B = {
      tail.fold(f(head, agg))(f)
    }
  }

  // TEST DATA
  val listSuperTiny = new Cons[Int](1, Empty)
  val listTiny = new Cons[Int](1, new Cons(2, Empty))
  val listTinyToBeZipped = new Cons[Int](10, new Cons(20, Empty))
  val list1 = new Cons[Int](1, new Cons(2, new Cons(3, new Cons(4,
    new Cons(5, new Cons(6, new Cons(7, new Cons(8, new Cons(9, new Cons(10, new Cons(11, Empty)))))))))))
  println(list1.toString2)

  // Filter function test
  println(
    list1.filter(new MyPredicate[Int] { def test(elem: Int): Boolean = if (elem % 2 == 0) true else false }).toString2
  )

  // Map function test
  println(
    list1.map(
      new MyTransformer[Int, Int] {def transform(x: Int): Int = x * 100}
    ).toString2
  )

  def map100Factor: MyTransformer[Int, Int] = new MyTransformer[Int, Int] {
    override def transform(inputElem: Int): Int = inputElem * 100
  }

  def flatMapTransformer: MyTransformer[Int, MyList[Int]] = new MyTransformer[Int, MyList[Int]] {
    override def transform(inputElem: Int): MyList[Int] = new Cons[Int](inputElem * 100, new Cons(inputElem, Empty))
  }

  assert(
    listSuperTiny.map(map100Factor).head == 100 &&
    listSuperTiny.map(map100Factor).tail == Empty &&
      listTiny.map(map100Factor).head == 100 && listTiny.map(map100Factor).tail.head == 200 &&
      listTiny.map(map100Factor).tail.tail == Empty,
    "Map on Super tiny list & Tiny List doesn't return the right result"
  )

  assert(
    listSuperTiny.flatMap(flatMapTransformer).head == 100 &&
      listSuperTiny.flatMap(flatMapTransformer).tail.head == 1 &&
      listSuperTiny.flatMap(flatMapTransformer).tail.tail == Empty,
    s"FlatMap on Super Tiny list doesn't return the right value - here's what it returned ${listSuperTiny.flatMap(flatMapTransformer)}"
  )

  println("HOFs exercises for foreach, sort, zipWith and fold")
  println("foreach()")
  listSuperTiny.foreach((x: Int) => println(x))
  list1.foreach((x: Int) => println(x))

  println("sort")
  // println(list1.sort((a: Int, b: Int) => a - b).toString2)

  println("zipWith")
  println(listTiny.zipWith(listTinyToBeZipped, (a: Int, b: Int) => a+b).toString2)
  println(listTiny.zipWith(listTinyToBeZipped, (a: Int, b: Int) => a.toString + "_" + b.toString).toString2)

  println("fold")
  println(listTiny.fold(10)((a, b) => a + b))
  assert(
    listTiny.fold(10)((a, b) => a + b) == 13,
    "Fold result should be 13"
  )
}
