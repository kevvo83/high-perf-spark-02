package proj.scalaadvlrn

object PartialFunctions extends App {

  val pf1: PartialFunction[Int, String] = new PartialFunction[Int, String] {
    override def apply(v1: Int): String = v1 match {
      case 1 => "1"
      case 2 => "2"
      case 100 => "100"
    }

    override def isDefinedAt(x: Int): Boolean =
      if (x == 1 || x == 2 || x == 100) true else false
  }

  // Syntax sugar of the above is below

  val pf2: PartialFunction[Int, String] = {
    case 1 => "1"
    case 2 => "2"
    case 100 => "100"
  }
}
