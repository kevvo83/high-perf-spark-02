val x = List("20-01-2013", "21-02-2014", null)

val y = List(x)
// y.flatMap(x.filter())

def testIfNull(e: Any): Boolean =
  e match {
    case null => false
    case _ => true
  }

x.filter(testIfNull(_))