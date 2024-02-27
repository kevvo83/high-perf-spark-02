package proj.scalaadvlrn

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.Random

object Futures extends App {

  case class Profile(id: String, name: String)

  object Network {

    val userIdToName: mutable.Map[String, String] = mutable.Map.apply(
      "fb.1" -> "mark",
      "fb.2" -> "bill",
      "fb.3" -> "zach",
      "fb.000" -> "Dummy Profile"
    )

    val friendsMap: mutable.Map[String, String] = mutable.Map.apply(
      "fb.1" -> "fb.2",
      "fb.2" -> "fb.3"
    )

    def fetchProfileById(id: String): Future[Profile] = Future {
      Profile(id, userIdToName(id))
    } recoverWith {
      case e: Throwable => fetchProfileById("fb.000")
    }

    def fetchBestFriend(id: String): Future[Profile] = Future {
      Profile(friendsMap(id), userIdToName(friendsMap(id)))
    } recoverWith {
      case e: Throwable => fetchProfileById("fb.000")
    } // TODO: NOTE - use recoverWith with Future to specify recovery in case Future throws an exception

    def poke(p1: Profile, p2: Profile): Unit = Future {
      println(s"Profile ${p1.name} has poked ${p2.name}")
    }
  }

  import Network._

  val res1: Future[Unit] = for {
    m <- fetchProfileById("fb.1")
    bf <- fetchBestFriend("fb.1")
  } yield poke(m, bf) // this for-comprehension evaluates to below

  val res2: Future[Unit] = fetchProfileById("fb.1").flatMap(
    m => fetchBestFriend("fb.1").map(bf => poke(m, bf))
  ) // this flatmap expression evaluates to the above


  // Test fallback methods (which were implemented using recoverWith - case where both are dummy profile
  for {
    x <- fetchProfileById("fb.x")
    bf <- fetchBestFriend("fb.x")
  } yield poke(x, bf)

  // Test fallback method - case where real person has dummy profile as friend
  for {
    x <- fetchProfileById("fb.3")
    bf <- fetchBestFriend("fb.1231")
  } yield poke(x, bf)



}
