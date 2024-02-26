package proj.scalaadvlrn

object PatternMatching extends App {

  // Case 1
  // Pattern Matching to decompose a class - using the UNAPPLY() method
  // The object singleton Person pulls in the UNAPPLY method to be in scope
  class Person(val name: String, val age: Int)
  object Person {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
  }

  val bob: Person = new Person("bob", 45)
  bob match {
    case Person(name, age) => s"This is $name and he's $age years old"
  }

  // Case 2
  // Pattern Matching to create singleton objects with unapply methods that do custom decomposing
  val n: Int = 20

  object EvenNumber {
    def unapply(num: Int): Boolean = if (num % 2 == 0) true else false
  }

  object SingleDigit {
    def unapply(num: Int): Boolean = if (num < 10) true else false
  }

  println(
      n match {
      case EvenNumber() => s"$n is an even number"
      case SingleDigit() => s"$n is a single digit number"
      case _ => "no discernable status"
    }
  )



}
