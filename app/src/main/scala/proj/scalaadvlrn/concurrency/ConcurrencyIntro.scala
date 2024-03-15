package proj.scalaadvlrn.concurrency

import java.util.concurrent.Executors

object ConcurrencyIntro extends App {

  // Thread Pool
  val threadPool = Executors.newFixedThreadPool(10)
  threadPool.execute(() => println("First Runnable"))

  // start / join methods of Thread
  /*
    val aThread = new Thread(() => println("..."))
    aThread.start() // starts the process in a separate thread
    aThread.join() // waits for the thread to complete
   */


  // Exercise 1 - Inception Threads
  def _implInceptionThreads(iter: Int, max: Int): Thread = new Thread(() => {
    if (max - iter > 0) {
      val newThread: Thread = _implInceptionThreads(iter + 1, max)
      newThread.start()
      newThread.join()
    }
    println(s"Hello from Thread Number #$iter")
  })
  _implInceptionThreads(1, 5).start()

  // Exercise 2
  var x = 0
  (1 to 1000).map(_ => new Thread(() => x += 1)) foreach {
    (t: Thread) => t.start()
  }
  println(s"Final value of x is $x")
  // What is the Maximum value possible of x? Ans: 100
  // What is the Minimum value possible of x? Ans: 1


  // Using the synchronized keyword
  class BankAccount(val accountName: String, var balance: Int)

  def transaction(account: BankAccount, amount: Int): Unit = {
    account.synchronized({
      account.balance -= amount
    })
  }

  val bankAccount: BankAccount = new BankAccount("Kevin", 200)
  List(10, 20, 30, 50, 40).map(amt => new Thread(() => transaction(bankAccount, amt))).foreach(_.start())

  Thread.sleep(1000) // TODO: How to replace this sleep?
  assert(bankAccount.balance == 50)
  println(s"Closing balance of my bank account is ${bankAccount.balance}") // should be 50

  val bankAccount2: BankAccount = new BankAccount("Jess", 200)
  List(10, 20, 30, 50, 40).foreach(
    amt => threadPool.execute(() => transaction(bankAccount2, amt))
  )
  Thread.sleep(1000) // TODO: How to replace this sleep?
  assert(bankAccount2.balance == 50)

  threadPool.shutdownNow() // stops accepting new runnables - as opposed to shutDownNow - which shuts down the pool

  // when you say someVar.synchronized(), you're calling the function inherited from AnyRef
  // Primitives such as Int, Boolean don't have synchronized expressions.

}
