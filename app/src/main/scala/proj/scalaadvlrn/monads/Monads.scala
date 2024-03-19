package proj.scalaadvlrn.monads

object Monads extends App {

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }
  object Attempt {
    def apply[T](x: => T): Attempt[T] =
      try {
        Success(x)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Success[+X](value: X) extends Attempt[X] {
    def flatMap[B](f: X => Attempt[B]): Attempt[B] =
      try f(value) catch {
        case e: Throwable => Failure(e)
      }
  }
  case class Failure(exp: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  // I Skipped all of the theoretical stuff exploring the properties of Monads - too impatient
  // Properties such as right-something and associativity
  // Must go through if I have issues with Monads moving forward

  // Exercise 1 - implement a Lazy monad
  /*
    Lazy abstracts away a computation that will only be executed when it's needed
    Implement unit/apply and flatMap
   */
  trait Lazy[T] {
    def use: T
    def flatMap[U](f: (=> T) => Lazy[U]): Lazy[U]
  }
  object Lazy {
    def apply[T](x: => T): Lazy[T] = new LazyValue(x) // apply() is called unit() in other languages
  }

  sealed class LazyValue[A](value: => A) extends Lazy[A] {
    override def flatMap[U](f: (=> A) => Lazy[U]): Lazy[U] = f(containedValue)
    def use: A = containedValue
    lazy val containedValue: A = value
  }

  val lazyMonadInst = Lazy{
    println("Log output when the monad instance is instantiated") // This should print out second
    20
  }
  val lazyMonadInstNumber2 = lazyMonadInst.flatMap (
    x => {
      println("Log at flatMap lambda run") // This should print out first
      Lazy(x * 20)
    }
  )

  println(lazyMonadInstNumber2.use)
  println(lazyMonadInst.use) // should not print out the "log output when monad ... instantiated" message - as it's already been evaluated once

  // AWWWW YEAHHHHHHHHHHHHHH


  // Exercise 2
  /*
    Monads = unit and flatMap
    Monads = unit + map + flatten

    Monad[T]{
      Given the implementation of flatMap .....
      def flatMap[B](f: T => Monad[B]): Monad[B] = ???

      How would you implement map() and flatten() below ?
      def map[B](f: T => B): Monad[B] = ??
      def flatten(m: Monad[Monad[T]]): Monad[B] = ???
    }
   */

  abstract class MrMonadic[T](value: => T){
    def flatMap[U](f: (=> T) => MrMonadic[U]): MrMonadic[U] = f(containedVal)
    def map[U](f: (=> T) => U): MrMonadic[U] = flatMap(x => MrMonadic(f(x)))
    def flatten(m: MrMonadic[MrMonadic[T]]): MrMonadic[T] = m.flatMap((x: MrMonadic[T]) => x)
    private lazy val containedVal: T = value
    def use: T = containedVal
  }
  object MrMonadic {
    def apply[A](value: => A) = new MrMonad(value)
  }

  class MrMonad[U](value: => U) extends MrMonadic[U](value)

  // TODO: Continue testing this!!!

}
