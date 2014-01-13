package algorithm

sealed trait Problem {
  def name: String
  def domain: String
  def className: String
}
case object Rastrigin extends Problem {
  val name = "Rastrigin"
  val domain = "R(-5.12:5.12)^30"
  val className = "net.sourceforge.cilib.functions.continuous.unconstrained.Rastrigin"
}
case object Hyperellipsoid extends Problem {
  val name = "Hyperellipsoid"
  val domain = "R(-5.12:5.12)^30"
  val className = "net.sourceforge.cilib.functions.continuous.unconstrained.HyperEllipsoid"
}
case object Rosenbrock extends Problem {
  val name = "Rosenbrock"
  val domain = "R(-2.048:2.048)^30"
  val className = "net.sourceforge.cilib.functions.continuous.unconstrained.Rosenbrock"
}
case object Spherical extends Problem {
  val name = "Spherical"
  val domain = "R(-5.12:5.12)^30"
  val className = "net.sourceforge.cilib.functions.continuous.unconstrained.Spherical"
}
case object Schwefel extends Problem {
  val name = "Schwefel"
  val domain = "R(-512.03:511.97)^30"
  val className = "net.sourceforge.cilib.functions.continuous.unconstrained.Schwefel"
}
case object Colville extends Problem {
  val name = "Colville"
  val domain = "R(-10:10)^4"
  val className = "net.sourceforge.cilib.functions.continuous.unconstrained.Colville"
}
case object Bohachevsky1 extends Problem {
  val name = "Bohachevsky1"
  val domain = "R(-100:100)^2"
  val className = "net.sourceforge.cilib.functions.continuous.unconstrained.Bohachevsky1"
}

object Termination extends Enumeration {
  type Termination = Value
  val Generations, Time = Value
  val stringTerminationMap = Map(values.map(e => (e.toString(), e)).toSeq: _*)
}

object Common {
  val Problems: Seq[Problem] = Seq(Rastrigin, Hyperellipsoid, Rosenbrock, Spherical, Schwefel, Colville, Bohachevsky1)
  val StringProblemMap = Map(Problems.map(e => (e.name, e)): _*)
}
