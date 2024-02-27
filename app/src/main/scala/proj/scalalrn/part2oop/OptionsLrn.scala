package proj.scalalrn.part2oop

import java.util.Random
import scala.collection.mutable

object OptionsLrn extends App {

  val config: mutable.Map[String, String] = mutable.Map(
    "host" -> "127.7.0.1",
    "port" -> "8801"
  )

  class Connection(val host: String, val port: String) {
    def connect(): Unit = s"Connected!"
  }
  object Connection {
    val random = new Random()
    def apply(host: String, port: String): Option[Connection] = {
      if (random.nextBoolean()) Some(new Connection(host, port))
      None
    }
  }

  // Option & For-Comprehension example 1
  val c: Option[Connection] = config.get("host").flatMap(
    h => config.get("port").flatMap(
      p => Connection(h, p)
    )
  ) map (c => c)

  val d: Option[Connection] = for {
    h <- config.get("host")
    p <- config.get("port")
    c <- Connection(h, p)
  } yield c

  // For-comprehension example 2
  val l1 = List(3, 8)
  val l2 = List(3, 4, 8)

  val res = for {
    la <- l1
    lb <- l2
  } yield la * lb
  println("res is" + res)


}