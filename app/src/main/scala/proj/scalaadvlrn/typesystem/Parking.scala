package proj.scalaadvlrn.typesystem

trait Vehicle
trait Bike extends Vehicle
trait Car extends Vehicle

sealed trait IParking[T] { // Invariant API
  def park(vehicle: T): IParking[T]
  def impound(vehicles: List[T]): IParkingImpl[T]
  def checkVehicles(conditions: T => Boolean): List[T]
  val databaseOfParks: List[T]
}

class IParkingImpl[T](db: List[T]) extends IParking[T] {
  override def park(vehicle: T): IParkingImpl[T] = IParkingImpl[T](vehicle :: databaseOfParks)
  override def impound(vehicles: List[T]): IParkingImpl[T] = IParkingImpl(vehicles ::: databaseOfParks)
  override def checkVehicles(conditions: T => Boolean): List[T] = databaseOfParks.filter(conditions)

  val databaseOfParks: List[T] = db

  def flatMap[U](f: T => IParkingImpl[U]): IParkingImpl[U] = ???
}

object IParkingImpl {
  def apply[T](db: List[T]) = new IParkingImpl[T](db)
}

trait CParking[+T] { // Covariant API
  def park[U >: T](vehicle: U): CParking[U]
  def impound[U >: T](vehicles: List[U]): CParking[U]
  def checkVehicles(conditions: T => Boolean): List[T]

  val databaseOfParks: List[T]
}

class CParkingImpl[+T](db: List[T]) extends CParking[T] {
  // "Widening our type" - i.e. widening the type of the collection
  override def park[U >: T](vehicle: U): CParking[U] = CParkingImpl(vehicle :: databaseOfParks)
  override def impound[U >: T](vehicles: List[U]): CParking[U] = CParkingImpl(vehicles ::: databaseOfParks)
  override def checkVehicles(conditions: T => Boolean): List[T] = databaseOfParks.filter(conditions)

  val databaseOfParks: List[T] = db

  def flatMap[U](f: T => CParkingImpl[U]): CParkingImpl[U] = ???
}

object CParkingImpl {
  def apply[T](db: List[T]) = new CParkingImpl[T](db)
}

trait XParking[-T] { // Contravariant API
  def park(vehicle: T): XParking[T]
  def impound(vehicles: List[T]): XParking[T]
  def checkVehicles[U <: T](conditions: U => Boolean): List[U]

  // val databaseOfParks: List[] TODO: Fix this
}

class XParkingImpl[-T](db: List[T]) extends XParking[T] {
  override def park(vehicle: T): XParking[T] = XParkingImpl(vehicle :: db)
  override def impound(vehicles: List[T]): XParking[T] = XParkingImpl(vehicles ::: db)
  override def checkVehicles[X](conditions: X => Boolean): List[X] = ??? // TODO: Fix this

  // override val databaseOfParks: List[X] = db // TODO: Fix this - related to the other 2 TODO's in this file

  def flatMap[U <: T, S](f: U => XParkingImpl[S]): XParkingImpl[S] = ???
}

object XParkingImpl {
  def apply[T](db: List[T]) = new XParkingImpl[T](db)
}


// Use Invariant List IList below as the db implementation for COVARIANT and CONTRAVARIANT parking
class IList[T]

class CParkingImpl2[+T](db: IList[T]) {
  def park[U >: T](vehicle: U): CParkingImpl2[U] = CParkingImpl2(db)
  def impound[U >: T](vehicles: IList[U]): CParkingImpl2[U] = CParkingImpl2(db)
  def checkVehicles[U >: T](conditions: T => Boolean): IList[U] = ???
}
object CParkingImpl2{
  def apply[T](db: IList[T]) = new CParkingImpl2[T](db)
}

class XParkingImpl2[-T](db: IList[T]) {
  def park(vehicle: T): XParkingImpl2[T] = XParkingImpl2(db)
  def impound[U <: T](vehicles: IList[U]): XParkingImpl2[U] = XParkingImpl2(db)
  def checkVehicles[U <: T](conditions: U => Boolean): List[U] = ???
}
object XParkingImpl2 {
  def apply[T](db: IList[T]) = new XParkingImpl2(db)
}

