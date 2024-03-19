package proj.scalaadvlrn.curriesandfunctions

object PartialFunctions extends App {

  // Partial Functions can have only 1 input parameter type

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

  val pf3: PartialFunction[Int, String] = {
    case 1 => "1"
    case 3 => "3"
    case 1121 => "1121"
  }

  println(pf2.isDefinedAt(101))
  println(pf2(100))
  if (pf2.isDefinedAt(101)) pf2(101)
  println(pf3.isDefinedAt(101))

  // Can lift a Partial Function into a Total function that returns Option
  val totalFunc3 = pf3.lift
  assert(totalFunc3(101).isEmpty)
  assert(totalFunc3(1121).isDefined)

  // Can chain multiple Partial Functions using `orElse`
  assert(!pf2.orElse(pf3).isDefinedAt(1000))

  assert(
    pf2.orElse[Int, String]{
      case 999 => "999"
      case 444 => "444"
    }.isDefinedAt(999)
  )

  // Partial Functions extend Total Functions
  // HOFs accept Partial Functions as well!!

  println(List(1,2,100) map (pf2(_)))
  println(
    List(1,2,100) map {
      case 1 => "1"
      case 2 => "2"
      case 100 => "100"
    }
  )

  // 1. Construct a PF instance - by instantiating the PF Trait - anonymous class
   val exerPf1 = new PartialFunction[Int, Int] {
     override def isDefinedAt(x: Int): Boolean = try{
       apply(x)
       true
     } catch {
       case _: Exception => false
     }

     override def apply(v1: Int): Int = v1 match {
       case 100 => 100
       case 200 => 200
       case 10000 => 10000
     }
   }
  assert(exerPf1(100) == 100)
  assert(exerPf1(10000) == 10000)
  assert(exerPf1.isDefinedAt(10000))
  assert(!exerPf1.isDefinedAt(1000000000))


  // 2. implement a dumb chatbot using a PF
  scala.io.Source.stdin.getLines().foreach {
    case "hello" => println("hi, how are you?")
    case "good thanks" => println("that's great - how can I help?")
  }

}
