package algorithm

object Problem extends Enumeration {
  type Problem = Value
  val Rastrigin, Hyperellipsoid, Rosenbrock, Spherical, Schwefel, Colville, Bohachevsky1 = Value
  val stringProblemMap = Map(values.map(e => (e.toString(), e)).toSeq: _*)
}

object Termination extends Enumeration {
  type Termination = Value
  val Generations, Time, Quality = Value
  val stringTerminationMap = Map(values.map(e => (e.toString(), e)).toSeq: _*)
}
