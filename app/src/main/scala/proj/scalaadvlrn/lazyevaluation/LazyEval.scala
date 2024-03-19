package proj.scalaadvlrn.lazyevaluation

object LazyEval extends App {

  // LazyEval is evaluated only once, but only when they are used for the first time
  // Call-by-need - as opposed to call-by-name which is also evaluated only when needed, but can be eval > 1 time

  // Be careful when using Lazy Eval in the following situations:
  // 1. when your lazy evaluated block has Side-effects
  // 2. When using lazy eval together with call-by-name

}
