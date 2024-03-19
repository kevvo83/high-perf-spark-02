package proj.scalaadvlrn.curriesandfunctions

object CurriesPartiallyAppFunc extends App {

  // Functions vs. Methods
  /*
    1. Methods are functions that are part of a class structure
      * Cannot have anonymous methods
    2. Functions are defined outside of classes
   */

  def curriedAdder(a: Int)(b: Int): Int = a + b
  val superAdder: Int => Int => Int = a => b => a + b

  val sum5: Int => Int = curriedAdder(5) // Need to specify the type of sum5 here - otherwise compiler will complain
  // When I specify the type of sum5, that tells the compiler that I want the remainder of the function after applying the first parameter list
  // When you call a method, you need to provide all of the argument parameter lists
  // When you specify the type above, that tells the compiler that I want to LIFT the method curriedAdder to a function val
  println(sum5(10))

  // LIFTING is the process of transforming a method to a function
  // ETA-Expansion is the technical term for lifting
  // functions != methods
  def inc(x: Int): Int = x + 1
  List(1,2,3).map(inc) // Compiler does ETA-expansion for us, an converts .map(inc) to .map(x => inc(x))

  // The underscore forces the compiler to do an ETA Expansion for me
  // Need to add the underscore here - otherwise the compiler will complain
  val sum3 = curriedAdder(3) _
  println(sum3(10))

  // TODO: Note below about PAF (Partially Applied Functions) and LIFTING/ETA-Expansion
  // LIFTING (ETA-Expansion) forces the compiler to turn a method/function into a Function val
  // Functions and Methods can both be PARTIALLY APPLIED using underscores (or some other syntax)
  // PARTIALLY APPLIED FUNCTIONS means that functions that are not called with their full parameter list

  // Exercise 1
  // Setup
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int): Int = x + y
  def curriedAddMethod(x: Int)(y: Int): Int = x + y

  // define add7 function val - add7: Int => Int = y => y + 7:
  // - implement out of the above 3 add function/method versions - as many as possible
  val add7_1 = (x: Int) => simpleAddFunction(x, 7)
  val add7_3 = (x: Int) => simpleAddMethod(x, 7)
  val add7_4 = (x: Int) => curriedAddMethod(7)(x)

  // Partially Applied functions - i.e. where first called without full parameter list
  val add7_2: Int => Int = curriedAddMethod(7)
  val add7_6 = curriedAddMethod(7)(_) // alternative syntax for curriedAddMethod(7) _ - i.e. for turning method into Function val

  val add7_7 = simpleAddMethod(7, _: Int) // alternative syntax for LIFTING method into Function vals
  // Compiler rewrites the above as:
  // val add7_7 = y => simpleAddMethod(7, y)
  // I can apply this same syntax and LIFT to a Function as well!
  val add7_8 = simpleAddFunction(7, _: Int) // Compiler lifts this Function into below:
  // val add7_8 = y => simpleAddFunction(7, y)

  val add7_5 = simpleAddFunction.curried(7) // Interesting!!!! - use of the curried method on the Function2


  def concatenator(a: String, b: String, c: String): String = a + b + c
  val logger1 = concatenator("LOGLEVEL", _: String, _: String) // LIFT method into Function val
  logger1("Timestamp here", "Message here")


  // Exercise
  // 1. Process a list of numbers and return string rep with different formats
  // Use the $4.2f, %8.6f, $14.12f - with a curried formatter function - takes a format and a number - then apply that formatter on a List of numbers as a HOF
  def formatter(f: String)(number: Double): String = f.format(number)

  val listFormats: List[String] = List("%4.2f", "%8.6f", "%14.12f")
  val listFormatters = List(formatter("%4.2f")(_), formatter("%8.6f")(_), formatter("%14.12f")(_))
  val input: List[Double] = List(Math.PI, 121.123123, 99992.11, 881.1e-12)

  println(input.flatMap(elem => listFormats.map(x => formatter(x)(elem))))
  println(input.flatMap(elem => listFormatters.map(f => f(elem))))

  // 2. difference between:
  // - functions vs methods
  // - paramters: by-name vs 0-lambda
  def byName(n: => Int): Int = n + 1
  def byFunction(f: () => Int) = f()

  def method: Int = 42 // Accessor methods without parenthesis - compiler will not do ETA expansion
  def parenMethod(): Int = 42 // Proper method with parenthesis - compiler will do ETA expansion to convert this to a Function Val

  // Call by-name vs. call by-value - https://www.baeldung.com/scala/parameters-by-value-by-name

  byName(method) // `method` is evaluated to 42 within the `byName` body when it needs to be used
  byName(parenMethod()) // `parenMethod` is evaluated to 42 within the `byName` body when it needs to be used
  byName(32) // 32 is evaluated when needed within the method body
  byName((() => 32)()) // You define an anonymous function and call it in the parameter list to `byName`

  // Unlike methods, you cannot just pass the Function Value to the `byName` parameter list - it won't evaluate the value and return it
  // You will need to CALL the function by passing it its parameters
  // byName(parenMethod) this doesn't compile
  byName(parenMethod()) // this does - and is evaluated within the body of the `byName` function


  byFunction(() => 2 * 2) // Passing a Function Value here works
  // byFunction((() => 2 * 2)()) - this doesn't work, because the function call will evaluate to int within the parameter list
  byFunction(() => 34)
  // byFunction(method) - not OK - as method evaluates to the value 42
  byFunction(parenMethod) // compiler lifts this to a Function val
  // byFunction(parenMethod()) - this doesn't work as parenMethod() will evaluate within the parameter list of `byFunction`


  def greaterThan30_method(i: Int): Boolean = i > 30
  println(List(10, 20, 30, 32, 40, 50).filter(greaterThan30_method)) // lifted into a function

  val greaterThan30_func: Int => Boolean = (i: Int) => i > 30
  println(List(10, 20, 30, 32, 40, 50).filter(greaterThan30_func)) // actually IS a function

}
