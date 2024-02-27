package proj.scalaadvlrn

import java.util.Random
import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import com.typesafe.scalalogging.Logger

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

object Futures2 extends App {

  // data points
  class Persona(val name: String, val id: String)
  class User(val userName: String, val userId: String) extends Persona(userName, userId)
  class Merchant(val merchantName: String, val merchantId: String) extends Persona(merchantName, merchantId)

  case class Transaction(
                        sender: Persona,
                        beneficiary: Persona,
                        amount: Double,
                        status: String
                      )
  val dummyTransaction: Transaction = Transaction(
                                        new User("xyz", "0"),
                                        new Merchant("xyz", "0"),
                                        0.00, "FAIL"
                                      )

  // Backend Purchase service that's part of a banking app backend
  class PurchaseService(val bankName: String = "", val userService: UserService)
                       (implicit concurrentExecutionContext: scala.concurrent.ExecutionContext) {

    import PurchaseService._

    private def createTransaction (
       sender: Persona, beneficiary: Persona, amount: Double
    ): Future[Either[Throwable, Transaction]] =
    Future {
      try {
        // Transaction checks are performed here
        if (randomizer.nextBoolean()) Right(Transaction(sender, beneficiary, amount, "SUCCESS"))
        else throw new RuntimeException("Create Transaction checks failed")
      } catch {
        case e: Throwable => Left(e)
      }
    }

    // Step 1 - Create Transaction + Check if the transaction
    // Fetch the users from the DB
    // Create a TXN
    // Wait for the TXN to finish
    def purchase(buyerId: String, merchantId: String, amount: Double): String = {

      // TODO: I've used Future[Either] here - but it was a PITA - I couldn't use For-Comprehension for this
      // TODO: Look into Validated and maybe Future[Validated] - is there a way to get them to play nice together?

      val userM = for {
        u <- userService.fetchUserById(buyerId)
        m <- userService.fetchUserById(merchantId)
      } yield (u, m)

      val txnF = userM.flatMap({
        case (Right(user: Persona), Right(merchant: Persona)) => createTransaction(user, merchant, amount)
        case _ =>
          logger.info("Failure to get user or merchant based on IDs")
          Future(Left(new Exception("Could not find user or merchant based on IDs")))
      })

      Await.result(txnF, 5.seconds) match {
        case Right(txn: Transaction) => txn.status
        case Left(e) =>
          logger.info(s"Create transaction failed with error - ${e.getMessage}")
          "FAILED"
      }
    }
  }

  object PurchaseService {
    import scala.concurrent.ExecutionContext.Implicits.global

    def apply(name: String, userService: UserService) = new PurchaseService(name, userService)
    val randomizer = new Random()
    val logger: Logger = Logger(getClass.getName)
  }

  class UserService(val dbConnection: String)
                   (implicit concurrentExecutionContext: scala.concurrent.ExecutionContext)
  {
    import UserService._

    def fetchUserById(id: String): Future[Either[Throwable, Persona]] = {
      Future {
        try {
          userTable.get(id) match {
            case a: Some[String] =>
              logger.info(s"User of $id lookup succeeded")
              Right(new Persona(id, a.get))
            case _ =>
              logger.info(s"User of $id lookup failed")
              throw new RuntimeException("No user found with that ID") // simulate db lookup exception
          }
        } catch {
          case e: Throwable => Left(e)
        }
      }
    }
  }

  object UserService {
    val logger: Logger = Logger(getClass.getName)
    val userTable: mutable.Map[String, String] = mutable.Map.apply(
      "fb.1" -> "mark",
      "fb.2" -> "bill",
      "fb.3" -> "zach",
      "fb.000" -> "Dummy Profile"
    )
  }

  // TESTS

  import scala.concurrent.ExecutionContext.Implicits.global

  val onlineStore = new PurchaseService("carousell", new UserService("postgresdbbackend"))

  println(s"Purchase result is: ${onlineStore.purchase("fb.1", "fb.3", 100.0)}")

  assert(onlineStore.purchase("fb.xyz", "fb.3", 100.0) == "FAILED")

}
