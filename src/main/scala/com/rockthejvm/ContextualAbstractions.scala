package com.rockthejvm

object ContextualAbstractions {

  // TODO update to Scala 3

  /*
    1 - context parameters/arguments
   */
  val aList = List(2, 1, 3, 4)
  val anOrderedList = aList.sorted // contextual argument: (descendingOrdering)

  // Ordering
  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _) // (a,b) => a > b
  // if a > b === (_ > _) then a should be before b === fromLessThan
  // given -> compiler will use given ordering instead of natural one that is predefined by Scala!

  // a given instance is analogous to an implicit val
  //
  trait Combinator[A] { // technically its a monoid
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using combinator: Combinator[A]): A = list.reduce((a, b) => combinator.combine(a, b))
  // "using" will indicate that it can find a combinator of that type, it will be "given" in scope.
  // So we don't need to pass it each time!
  given intCombinator: Combinator[Int] = new Combinator[Int] {
    override def combine(x: Int, y: Int) = x + y
  }
  val theSum = combineAll(aList) // (intCombinator)
  // ^^compile-time if not defined in scope(ie., if no intCombinator is written)

  /*
  Compiler looks for "given" instances in these places:
    - local scope
    - imported scope
    - the companions of all the types involved in the call. (In our case:
      - companion of List
      - the companion of Int -> finds it here!
      )
   */

  // context bounds
  def combineAll_v2[A](list: List[A])(using Combinator[A]): A = ???

  //  ===
  def combineAll_v3[A: Combinator](list: List[A]): A = ???
  // A: Combinator us called as -> A must have a "given" instance of this Monoid defined in scope
  /*
    where context args are useful
    - type classes
    - dependency injection
    - context-dependent functionality //using code for some types and not for others
    - type-level programming // compiler can generate types and find relations between types at compile-time
   */

  /*
    2 - extension methods
   */

  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name, I love Scala!"
  }

  extension(string: String)
    def greet(): String = new Person(string).greet()

  // ^^ greet is an extension method for String type.
  val danielsGreeting = "Daniel".greet() // "type enrichment"

  // POWER
  extension[A](list: List[A])
    def combineAllValues(using combinator: Combinator[A]):A = list.reduce(combinator.combine)

  val theSum_v2 = aList.combineAllValues

  def main(args: Array[String]): Unit = {
    println(anOrderedList)
    println(theSum)
    println(theSum_v2)
  }
}
