package proj.scalaadvlrn

import scala.util.Try

object SyntaxSugars extends App {

  // // // Syntax Sugar 1 - Single Abstract Method
  trait MultiThreadedProcess {
    val staticVal: Int = 100
    def run(operation: String): Unit
  }

  // Compiler hint to reduce this to Single Abstract Method
  val process: MultiThreadedProcess = new MultiThreadedProcess {
    override def run(operation: String): Unit = println(s"$operation")
  }

  // Compiler can figure out that the function provided is the definition for the run() method
  val process2: MultiThreadedProcess = (operation: String) => println(s"$operation")

  // Most useful in the case of defining Runnables
  // In Java, this is often done - Scala compiler says that this can be optimised to a Single Abstract method definition
  val processThread = new Thread(new Runnable {
    override def run(): Unit = println("Process running in a Thread")
  })
  val processThread2 = new Thread(() => println("Process running in a Thread")) // Much more intuitive to read, tbh




  // // // Syntax Sugar 2 - Methods using curly brace instead of brackets
  List(1, 2, 3).map {
    // can even do other stuff in here - maybe a side-effect???
    x => x * 3
  }

  /*
    Try {
    }
    The above is actually Try.apply { .... } - similar to the map() example above
   */



  // // // Syntax Sugar 3 - Functions that have colon are right associative - most functions (like map()) are left associative
  assert(3 :: List(1,2) == List(1,2).::(3)) // will evaluate to True



  // // // Syntax Sugar 4 - Infix of types (Infix of parameters Im familiar with, but Infix of Types - WTF?)
  class Composite[A, B]
  val compositeInstance: Int Composite Int = new Composite


}
