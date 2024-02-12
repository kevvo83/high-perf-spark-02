package proj.scalalrn.part2oop

object VariancePositions extends App {

  /*
    The Variance Question:
    If A is a subtype of B (i.e. B >: A) this is Thing[A] a subtype of Thing[B]?

    If yes, then Thing is COVARIANT in type argument T - i.e. Thing[+T]
    If no, then Thing is INVARIANT in type argument T - i.e. Thing[T]

    If yes but the in the opposite direction, then Thing is CONTRAVARIANT in type argument T - i.e. Thing[-T]
   */

  // COVARIANT example
  // Given that Dog is a subclass of Animal
  // is a List[Dog] a subtype of List[Animal]? - For Covariant, the answer is yes
  class MyCoVariantList[+T]
  class MyInvariantList[T]
  class MyContraVariantList[-T]

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  val animalList: MyCoVariantList[Animal] = new MyCoVariantList[Dog]

  // INVARIANT example
  // is a List[Dog] a subtype of List[Animal]? - For Invariant, the answer is no
  val animalList2: MyInvariantList[Animal] = new MyInvariantList[Animal]
  // val animalList2: MyInvariantList[Animal] = new MyInvariantList[Dog] - Type mismatch compiler error

  // CONTRAVARIANT example
  // is a List[Animal] a subtype of List[Dog]? - For Contravariant, the answer is yes
  val animalList3: MyContraVariantList[Dog] = new MyContraVariantList[Animal]
  // val animalList3: MyContraVariantList[Animal] = new MyContraVariantList[Dog] - type mismatch compiler error

  // Another Contravariant example
  class Vet[-T]
  val dogVet: Vet[Dog] = new Vet[Animal]

}
