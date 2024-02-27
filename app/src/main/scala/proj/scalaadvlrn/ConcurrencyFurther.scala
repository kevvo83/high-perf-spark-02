package proj.scalaadvlrn

import java.util.concurrent.Executors

object ConcurrencyFurther extends App {

  // Create Deadlock exercise
  case class Friend(name: String) {
    def bow(otherFriend: Friend): Unit =
      this.synchronized {
        println(s"$name is bowing to ${otherFriend.name}")
        otherFriend.bow(this)
        rise()
      }

    def rise(): Unit =
      this.synchronized {
        println(s"$name is rising after bowing")
      }
  }

  val threadPool = Executors.newFixedThreadPool(2)

  if (false){ // Deadlock example
    val sam: Friend = Friend("Sam")
    val pierre: Friend = Friend("Pierre")

    threadPool.execute(() => {
      sam.bow(pierre)
    })

    threadPool.execute(() => {
      pierre.bow(sam)
    })
  }

  // Create LiveLock - threads yield execution to each other, but they can't continue
  // TODO: Implement livelock exercise

  threadPool.shutdown()
}
