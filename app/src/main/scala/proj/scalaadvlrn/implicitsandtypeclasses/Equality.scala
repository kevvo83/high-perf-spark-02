package proj.scalaadvlrn.implicitsandtypeclasses

import proj.scalaadvlrn.implicitsandtypeclasses.TypeClasses.{Equal, User, user1, user2, user3}

object Equality extends App {

  case class User(name: String, email: String)
  val user1 = User("john", "john@google.com")
  val user2= User("philip", "philip@google.com")
  val user3= User("john", "john@google.com")

  // type class
  trait Equal[T] {
    def ===(current: T, other: T): Boolean
    def !==(current: T, other: T): Boolean
  }

  implicit object EqualityOfUsers extends Equal[User] {
    def ===(current: User, other: User): Boolean =
      current.name == other.name && current.email == other.email
    def !==(current: User, other: User): Boolean = !(===(current, other))
  }

  implicit object PartialEqualityOfUsers extends Equal[User] {
    def ===(current: User, other: User): Boolean =
      current.name == other.name || current.email == other.email
    def !==(current: User, other: User): Boolean = !(===(current, other))
  }

  implicit class UserOps[T](val input: T) extends AnyVal {
    def ===(other: T)(implicit equalityImplementation: Equal[T]): Boolean = equalityImplementation.===(input, other)
    def !==(other: T)(implicit equalityImplementation: Equal[T]): Boolean = equalityImplementation.!==(input, other)
  }

  // Can do this when there is only 1 implicit object that's applicable
  //  println(s"Equality check of user1 == user2 result: ${user1 === user2}")
  //  println(s"Equality check of user1 == user3 result: ${user1 === user3}")
  //  println(s"Equality check of user2 == user3 result: ${user2 === user3}")
  //  println(s"Equality check of user2 != user3 result: ${user2 !== user3}")

  // This is how I can specific the right implicit object to be used when there are > 1 applicable implicits available
  println(s"Partial Equality check of user1 === user 2: ${user1.===(user2)(PartialEqualityOfUsers)}")
  println(s"Partial Equality check of user1 === user 3: ${user1.===(user3)(PartialEqualityOfUsers)}")
  println(s"Full Equality check of user1 === user 3: ${user1.===(user3)(EqualityOfUsers)}")

}
