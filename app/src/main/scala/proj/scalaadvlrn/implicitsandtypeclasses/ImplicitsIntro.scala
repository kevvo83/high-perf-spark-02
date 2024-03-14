package proj.scalaadvlrn.implicitsandtypeclasses

object ImplicitsIntro extends App {

  case class Person(name: String, age: Int)
  object Person {
    // Most likely ordering to be specified in the companion object
    //    Can be overriden by importing another Ordering by doing an Import
    implicit val orderPeopleByNameDesc: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) > 0)
  }

  val listOfPeople = List(
    Person("a", 12),
    Person("z", 1),
    Person("s", 21),
    Person("s", 3)
  )

  /*
    Implicits scope - order of priority
     * Normal scope (aka local scope)
     * Imported scope
     * companion objects of all types involved in method signature
   */
  private object PersonAlphabeticOrderOnName {
    implicit val orderPeopleByAgeDesc: Ordering[Person] = Ordering.fromLessThan((f: Person, s: Person) => f.age > s.age)
    implicit val orderPeopleByAgeAsc: Ordering[Person] = orderPeopleByAgeDesc.reverse
  }

  import PersonAlphabeticOrderOnName.orderPeopleByAgeAsc
  println(listOfPeople.sorted)

}

