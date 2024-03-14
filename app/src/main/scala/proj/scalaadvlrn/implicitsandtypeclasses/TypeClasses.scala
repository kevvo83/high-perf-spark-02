package proj.scalaadvlrn.implicitsandtypeclasses

object TypeClasses extends App {

  case class User(name: String, email: String)
  val user1 = User("john", "john@google.com")
  val user2= User("philip", "philip@google.com")
  val user3= User("john", "john@google.com")

  // EXAMPLE 1

  // type class
  trait HTMLSerializer[T] {
    def serialize(input: T): String
  }

  // companion object
  object HTMLSerializer {
    def apply[T](input: T)(implicit serializerImplementation: HTMLSerializer[T]): String =
      serializerImplementation.serialize(input)
  }

  // type class instance
  implicit object UserToStringSerializer extends HTMLSerializer[User] {
    override def serialize(input: User): String =
      s"User name is ${input.name} and his/her/their email is ${input.email}"
  }

  println(HTMLSerializer[User](user1))
  println(HTMLSerializer[User](user2))

  // Is this how we add the HTML serializer behavior to the User class?? - yes, it seems like it is


  // EXAMPLE 2
  // type class
  trait Equal[T] {
    def apply(curr: T, other: T): Boolean
  }

  // Companion object of the Type Class
  object Equal {
    def apply[T](curr: T, other: T)(implicit equalityImplementer: Equal[T]): Boolean =
      equalityImplementer.apply(curr, other)
  }

  // type class instance
  implicit object UserEqualityCheck extends Equal[User] {
    override def apply(curr: User, other: User): Boolean =
      (curr.name == other.name) && (curr.email == other.email)
  }

  // Equal[User] This is equivalent to Equal.apply[User]
  println(Equal[User](user1, user2)) // equivalent to Equal.apply[User]()
  println(Equal.apply[User](user1, user2))
  println(Equal[User](user1, user3))


  // EXAMPLE 3

  trait isPrime[T] {
    def apply(input: T): Boolean
  }

  object isPrime {
    def apply[T](input: T)(implicit isPrimeImplementation: isPrime[T]): Boolean =
      isPrimeImplementation.apply(input)
  }

  implicit object IntIsPrimeImplementation extends isPrime[Int] {
    override def apply(input: Int): Boolean = {
      var res = true
      if (input == 2) res
      else if (input < 2) false
      else{
        (2 until input).foreach(elem => if (input % elem == 0) res = false)
        res
      }
    }
  }

  println(s"Is 2 prime?: ${isPrime[Int](2)}")
  println(s"Is 1 prime?: ${isPrime[Int](1)}")
  println(s"Is 5 prime?: ${isPrime[Int](5)}")
  println(s"Is 9 prime?: ${isPrime[Int](9)}")

  println(5 % 1)
}
