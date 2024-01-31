package proj.scalalrn.part2oop

/*
  Basic Interitance - looking at:
  * inheritance
  * overrides
  * protected, final keywords
  * auxiliary constructors vs. default constructor arguments
 */

object InheritanceBasics extends App {

  class Animal {
    def eats: String = "food"
    protected def eats2: String = "food"
    final def eats3: String = "chow"
  }

  val critter: Animal = new Animal
  println(critter.eats)
  // Cannot call the "protected" method eats2 - can only be called from within the class
  // critter.eats2

  class Dog extends Animal {

    // override overrides the implementation from the superclass
    override def eats: String = List(super.eats, "dog food").reduce(_ + ", " + _)
    // Cannot override the protected eats3 method
    // override def eats3: String = ""
  }

  val critter2: Animal = new Dog
  println(critter2.eats)

  class Primate(name: String, habitat: String) {

    final def defaultName = "ape"
    final def defaultHabitat = "jungle"
    def this(name: String) = this(name, defaultHabitat) // auxiliary constructor - alternative to default value for constructor args
    def this() = this(defaultName, defaultHabitat) // auxiliary constructor - alternative to default value for constructor args
  }

  class Primate2(name: String = "ape", habitat: String = "jungle")
}
