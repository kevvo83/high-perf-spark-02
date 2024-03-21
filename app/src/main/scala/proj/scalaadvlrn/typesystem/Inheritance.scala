package proj.scalaadvlrn.typesystem

object Inheritance extends App {

  trait Animal
  trait Cat extends Animal
  trait Dog extends Animal
  trait Kitty extends Cat

  class CCage[+T]
  class InvCage[T]
  class ContraVarCage[-T]

  /*
    SUMMARY:
    * Class Field defs are in COVARIANT position for VALs
    * Method arguments are in CONTRAVARIANT position
    * Method returns are in COVARIANT position
   */

  // 1. Explained

  // COVARIANCE explained
  val cageAnimal: CCage[Animal] = new CCage[Cat]

  // INVARIANCE explained
  // val cageAnimal2: InvCage[Animal] = new InvCage[Cat] // This won't compile - it'll fail
  val cageCat: InvCage[Cat] = new InvCage[Cat]

  // CONTRAVARIANCE explained
  val cageCat2: ContraVarCage[Cat] = new ContraVarCage[Animal]
  // val cageCat3: ContraVarCage[Animal] = new ContraVarCage[Cat] // This won't compile - it'll fail



  // 2. Covariant vs. Contravariant POSITIONS when defining Classes with field declarations

  // COVARIANT POSITIONS
  class CCage2[+T](val animal: T) // In the position of field declarations for a class, this is a Covariant position

  // In Covariant positions/places, the compiler will also accept Invariant types
  class InvCage2[T](val animal: T) // Invariant type accepted in Covariant position

  // In Covariant positions/places, the compiler will not accept Contravariant types
  // class XCage2[-T](val animal: T) // Compiler error - Contravariant type T appears in Covariant position

  // Covariant + Contravariant positions are basically a form of compiler restrictions



  // 3. Variance with Collections

  // Covariant collection example
  abstract class MyListType[+T] {
    def head: T
    def tail: MyListType[T]
  }


  // Example of a COVARIANT Collection
  class MyList[+T](val h: T, val t: MyListType[T]) extends MyListType[T]{
    // Method Argument is in a Contravariant position here - to go around this compiler restriction, ...
    //  you add [U >: T] and elem:U and make the method return a MyList[U]
    // Now you will get a wider collection - it has been widened from:
    //  Collection of Subtypes -> Collection of objects that meet the condition of BEING one of a SUPERTYPE
    def addElem[U >: T](elem: U) = new MyList[U](elem, this)
    override def head: T = h
    override def tail: MyListType[T] = t
  }

  object MyList {
    def apply[T](elem: T) = new MyList(elem, Empty)
  }

  object Empty extends MyListType[Nothing] {
    override def head: Nothing = throw new NoSuchElementException("No Head in an Empty List")
    override def tail: MyListType[Nothing] = this
  }

  // Tests
  val list1 = MyList(new Dog{})
  val list2 = list1.addElem(new Cat{}) // compiler makes the type of list2 to be MyList[Dog] - it goes up the inheritance chain until it finds the right type to retain the type hierarchy
  val list3 = list2.addElem(new Kitty{})



  // Example of a CONTRAVARIANT Collection
  /* abstract class XMyListType[-T] {
    def head[U <: T]: U
    def tail[U <: T]: XMyListType[U]
  }

  class XMyList[-T] extends XMyListType {
    override def head[U <: T]: U = ???
    override def tail[U <: T]: XMyListType[U] = ???
  }

  class M*/
}
